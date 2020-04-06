package indi.eos.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;

public interface StorageDriver
{
  String getName();

  byte[] getContent(String path) throws InvalidPathException, FileNotFoundException;

  void putContent(String path, byte[] content) throws InvalidPathException, FileNotFoundException, IOException;

  InputStream reader(String path, long offset) throws InvalidPathException, InvalidOffsetException, FileNotFoundException;

  OutputStream writer(String path, boolean append) throws InvalidPathException, IOException;

  StatEntity getStat(String path) throws InvalidPathException, FileNotFoundException;

  List<String> getList(String path) throws InvalidPathException, FileNotFoundException;

  void move(String sourcePath, String destPath) throws InvalidPathException, FileNotFoundException;

  void delete(String path) throws InvalidPathException, FileNotFoundException;

  String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException;

  void walk(String path, Consumer<StatEntity> consumer) throws InvalidPathException, FileNotFoundException;
}
