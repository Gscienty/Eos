package indi.eos.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;

import indi.eos.entities.RangeEntity;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.store.FileInfo;

public interface StorageDriver
{
  String name();

  byte[] get(String path) throws InvalidOffsetException, IOException, FileNotFoundException, EosUnsupportedException;

  void put(String path, byte[] content) throws EosUnsupportedException, IOException;

  InputStream reader(String path, long offset) throws IOException, InvalidOffsetException, FileNotFoundException, EosUnsupportedException;

  OutputStream writer(String path, boolean append) throws EosUnsupportedException, IOException;

  FileInfo getInfo(String path) throws FileNotFoundException, EosUnsupportedException;

  void move(String sourcePath, String destPath) throws FileNotFoundException, EosUnsupportedException;

  void delete(String path) throws FileNotFoundException, EosUnsupportedException;

  String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException, EosUnsupportedException;

  void walk(String path, Consumer<FileInfo> consumer) throws FileNotFoundException, EosUnsupportedException;
}
