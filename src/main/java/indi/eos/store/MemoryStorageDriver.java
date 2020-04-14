package indi.eos.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import java.util.function.Consumer;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.exceptions.UnexceptedException;
import indi.eos.store.FileInfo;
import indi.eos.store.StorageDriver;

final class MemoryStorageDriverFileOutputStream extends OutputStream {
  private MemoryStorageDriverFile file;

  public MemoryStorageDriverFileOutputStream(MemoryStorageDriverFile file, long offset) {
    this.file = file;
    this.file.setWritePosition(offset);
  }

  @Override
  public void write(int b) throws IOException {
    file.writeByte((byte) b);
  }
}

final class MemoryStorageDriverFileInputStream extends InputStream {
  private MemoryStorageDriverFile file;

  public MemoryStorageDriverFileInputStream(MemoryStorageDriverFile file, long offset) {
    this.file = file;
  }

  @Override
  public int read() throws IOException {
    return this.file.readByte();
  }
}

interface MemoryStorageDriverFileInfo extends FileInfo { }

class MemoryStorageDriverFile implements MemoryStorageDriverFileInfo {
  private String name;
  private String path;
  private List<byte[]> buffers;
  private long readPosition;
  private long writePosition;
  private long legalSize;
  private long modifyTime;

  public MemoryStorageDriverFile(String path, String name) {
    this.name = name;
    this.path = path;
    this.modifyTime = System.currentTimeMillis();
    this.buffers = new Vector<>();
    this.readPosition = 0;
    this.writePosition = 0;
    this.legalSize = 0;
  }

  public String name() {
    return this.name;
  }

  public String path() {
    return this.path;
  }

  public long size() {
    return this.legalSize;
  }

  public long modifyTime() {
    return this.modifyTime;
  }

  public boolean isDirectory() {
    return false;
  }

  public void setWritePosition(long offset) {
    this.writePosition = offset;
  }

  public void setReadPosition(long offset) {
    this.readPosition  = offset;
  }

  public void writeByte(byte b) {
    long nthBuffer = this.writePosition / 4096;
    while (nthBuffer >= this.buffers.size()) {
      this.buffers.add(new byte[4096]);
    }
    this.buffers.get((int) nthBuffer)[(int) (this.writePosition % 4096)] = b;
    this.writePosition++;
    if (this.writePosition > this.legalSize) {
      this.legalSize = this.writePosition;
    }
  }

  public int readByte() {
    if (this.readPosition > this.legalSize) {
      return -1;
    }
    long nthBuffer = this.readPosition / 4096;
    int result = this.buffers.get((int) nthBuffer)[(int) (this.readPosition % 4096)];
    this.readPosition++;
    return result;
  }

  public void truncate() {
    this.buffers.clear();
    this.legalSize = 0;
  }

  public InputStream reader(long offset) throws InvalidOffsetException {
    return new MemoryStorageDriverFileInputStream(this, offset);
  }

  public OutputStream writer(long offset) {
    return new MemoryStorageDriverFileOutputStream(this, offset);
  }
  public OutputStream writer() {
    return new MemoryStorageDriverFileOutputStream(this, this.legalSize);
  }
}

class MemoryStorageDriverDirectory implements MemoryStorageDriverFileInfo {
  private long modifyTime;
  private String name;
  private String path;
  private Map<String, MemoryStorageDriverFileInfo> child;

  public MemoryStorageDriverDirectory(String path, String name) {
    this.name = name;
    this.path = path;
    this.child = new HashMap<>();
    this.modifyTime = System.currentTimeMillis();
  }

  public String name() {
    return this.name;
  }

  public String path() {
    return this.path;
  }

  public long size() {
    return 0;
  }

  public long modifyTime() {
    return this.modifyTime;
  }

  public boolean isDirectory() {
    return true;
  }

  public void add(MemoryStorageDriverFileInfo info) {
    this.child.put(info.name(), info);
    this.modifyTime = System.currentTimeMillis();
  }

  public MemoryStorageDriverFileInfo find(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    if (path.equals("")) {
      return this;
    }
    int index = path.indexOf("/");
    String component;
    if (index < 0) {
      component = path;
    } else {
      component = path.substring(0, index);
    }

    MemoryStorageDriverFileInfo result = this.child.get(component);
    if (result == null) {
      return this;
    }

    if (result.isDirectory()) {
      return ((MemoryStorageDriverDirectory) result).find(path.substring(index + 1));
    }
    return result;
  }

  public MemoryStorageDriverFile touch(String path) throws UnexceptedException {
    MemoryStorageDriverFileInfo n = this.find(path);
    if (n.path().equals(path)) {
      if (n.isDirectory()) {
        throw new UnexceptedException();
      }
      return (MemoryStorageDriverFile) n;
    }
    int index = path.lastIndexOf("/");
    if (index < 0) {
      String name = path.substring(index + 1);
      MemoryStorageDriverFile file = new MemoryStorageDriverFile(String.format("%s/%s", this.path(), name), name);
      this.add(file);
      return file;
    }

    MemoryStorageDriverDirectory dir = this.mkdirs(path.substring(0, index));
    String name = path.substring(index + 1);
    MemoryStorageDriverFile file = new MemoryStorageDriverFile(String.format("%s/%s", dir.path(), name), name);
    dir.add(file);

    return file;
  }

  public MemoryStorageDriverDirectory mkdirs(String path) throws UnexceptedException {
    MemoryStorageDriverFileInfo n = this.find(path);
    if (!n.isDirectory()) {
      throw new UnexceptedException();
    }
    if (n.path().equals(path)) {
      return (MemoryStorageDriverDirectory) n;
    }
    String relative = path.substring(n.path().length());
    if (relative.startsWith("/")) {
      relative = relative.substring(1);
    }
    if (relative.equals("")) {
      return (MemoryStorageDriverDirectory) n;
    }
    MemoryStorageDriverDirectory curDirectory = (MemoryStorageDriverDirectory) n;

    for (String component : relative.split("/")) {
      MemoryStorageDriverDirectory createdDirectory = curDirectory.mkdir(component);
      curDirectory = createdDirectory;
    }

    return curDirectory;
  }

  public MemoryStorageDriverDirectory mkdir(String name) throws UnexceptedException {
    if (path.equals("")) {
      throw new UnexceptedException();
    }
    if (this.child.get(name) != null) {
      throw new UnexceptedException();
    }
    MemoryStorageDriverDirectory dir = null;
    if (this.path() == "/") {
      dir = new MemoryStorageDriverDirectory(String.format("%s%s", this.path(), name), name);
    } else {
      dir = new MemoryStorageDriverDirectory(String.format("%s/%s", this.path(), name), name);
    }
    this.add(dir);
    return dir;
  }

  public void delete(String path) throws UnexceptedException, FileNotFoundException {
    int index = path.lastIndexOf("/");
    if (index < 0) {
      this.child.remove(path);
    } else {
      String parentPath = path.substring(index);
      MemoryStorageDriverDirectory dir = (MemoryStorageDriverDirectory) this.find(parentPath);
      if (!dir.path().equals(parentPath)) {
        throw new FileNotFoundException();
      }
      dir.remove(path.substring(index + 1));
    }
  }

  public void remove(String name) throws FileNotFoundException {
    if (this.child.get(name) == null) {
      throw new FileNotFoundException();
    }
    this.child.remove(name);
  }
}

public class MemoryStorageDriver implements StorageDriver {
  MemoryStorageDriverDirectory root;

  public MemoryStorageDriver() {
    this.root = new MemoryStorageDriverDirectory("/", "");
  }

  public String name() {
    return "memory";
  }

  public byte[] get(String path) throws InvalidOffsetException, IOException, FileNotFoundException, EosUnsupportedException {
    MemoryStorageDriverFileInfo findedFileInfo = this.root.find(this.normalize(path));
    if (findedFileInfo.isDirectory()) {
      throw new FileNotFoundException();
    }
    MemoryStorageDriverFile file = (MemoryStorageDriverFile) findedFileInfo;
    InputStream input = file.reader(0);
    byte[] result = new byte[(int) file.size()];
    input.read(result);
    return result;
  }

  public void put(String path, byte[] content) throws EosUnsupportedException, IOException {
    path = this.normalize(path);
    try {
      MemoryStorageDriverFile file = this.root.touch(path);
      file.truncate();
      new MemoryStorageDriverFileOutputStream(file, 0).write(content);
    } catch (UnexceptedException ex) {
      throw new IOException();
    }
  }

  public InputStream reader(String path, long offset) throws IOException ,InvalidOffsetException, FileNotFoundException, EosUnsupportedException {
    path = this.normalize(path);
    try {
      MemoryStorageDriverFile file = this.root.touch(path);
      if (offset >= file.size()) {
        throw new InvalidOffsetException(this.name(), path, offset);
      }
      InputStream result = file.reader(offset);
      return result;
    } catch (UnexceptedException ex) {
      throw new IOException();
    }
  }

  public OutputStream writer(String path, boolean append) throws EosUnsupportedException, IOException {
    path = this.normalize(path);
    try {
      MemoryStorageDriverFile file = this.root.touch(path);
      if (!append) {
        file.truncate();
      }
      return file.writer();
    } catch (UnexceptedException ex) {
      throw new IOException();
    }
  }

  public OutputStream writer(String path, long offset) throws EosUnsupportedException, IOException {
    path = this.normalize(path);
    try {
      return this.root.touch(path).writer(offset);
    } catch (UnexceptedException ex) {
      throw new IOException();
    }
  }

  public FileInfo getInfo(String path) throws FileNotFoundException, EosUnsupportedException {
    path = this.normalize(path);
    MemoryStorageDriverFileInfo fileInfo = this.root.find(path);
    if (!fileInfo.path().equals(path)) {
      throw new FileNotFoundException();
    }
    return fileInfo;
  }

  public void move(String sourcePath, String destPath) throws FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void delete(String path) throws FileNotFoundException, EosUnsupportedException {
    path = this.normalize(path);
    try {
      this.root.delete(path);
    } catch (UnexceptedException ex) {
      throw new FileNotFoundException();
    }
  }

  public String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void walk(String path, Consumer<FileInfo> consumer) throws FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  private String normalize(String path) {
    return String.format("/%s", path);
  }
}
