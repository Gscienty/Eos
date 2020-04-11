package indi.eos.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import java.util.function.Consumer;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.store.StorageDriver;
import indi.eos.store.FileInfo;

class FsStorageDriverFileInfo implements FileInfo {
  private File file;

  public FsStorageDriverFileInfo(File file) {
    this.file = file;
  }

  public String path() {
    return this.file.getPath();
  }

  public long size() {
    return this.file.length();
  }

  public long modifyTime() {
    return this.file.lastModified();
  }

  public boolean isDirectory() {
    return this.file.isDirectory();
  }
}

public class FsStorageDriver implements StorageDriver {
  private static final String DEFAULT_ROOT_DIRECTORY = "/var/lib/registry";
  private final File rootDirectory;

  public FsStorageDriver(String rootDirectory) {
    this.rootDirectory = new File(rootDirectory);
  }

  public FsStorageDriver() {
    this.rootDirectory = new File(DEFAULT_ROOT_DIRECTORY);
  }

  public String name() {
    return "filesystem";
  }

  public byte[] get(String path) throws InvalidOffsetException, IOException, FileNotFoundException, EosUnsupportedException {
    File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    if (!file.isFile()) {
      throw new FileNotFoundException();
    }

    byte[] content = new byte[(int) file.length()];
    this.reader(path, 0).read(content);
    return content;
  }

  public void put(String path, byte[] content) throws EosUnsupportedException, IOException {
    this.writer(path, false).write(content);
  }

  public InputStream reader(String path, long offset) throws InvalidOffsetException, FileNotFoundException, EosUnsupportedException {
    File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }

    InputStream stream = new FileInputStream(path);
    try {
      stream.skip(offset);
    } catch (IOException ex) {
      throw new InvalidOffsetException(this.name(), path, offset);
    }

    return stream;
  }

  public OutputStream writer(String path, boolean append) throws EosUnsupportedException, IOException {
    File file = new File(path);
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }

    return new FileOutputStream(file, append);
  }

  public FileInfo getInfo(String path) throws FileNotFoundException, EosUnsupportedException {
    return new FsStorageDriverFileInfo(new File(path));
  }

  public void move(String sourcePath, String destPath) throws FileNotFoundException, EosUnsupportedException {
    File file = new File(sourcePath);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    file.renameTo(new File(destPath));
  }

  public void delete(String path) throws FileNotFoundException, EosUnsupportedException {
    File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    file.delete();
  }

  public String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void walk(String path, Consumer<FileInfo> consumer) throws FileNotFoundException, EosUnsupportedException {
    File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    if (file.isFile()) {
      return;
    }
    for (File child : file.listFiles()) {
      consumer.accept(new FsStorageDriverFileInfo(child));
    }
  }
}
