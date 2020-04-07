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
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;

public interface StorageDriver
{
  String getName();

  byte[] getContent(DigestEntity digest) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException;

  void putContent(DigestEntity digest, byte[] content) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException, IOException;

  InputStream reader(DigestEntity digest, long offset) throws EosInvalidDigestException, InvalidOffsetException, FileNotFoundException, EosUnsupportedException;

  OutputStream writer(DigestEntity digest, boolean append) throws EosInvalidDigestException, EosUnsupportedException, IOException;

  OutputStream writer(UUIDEntity uuid, boolean created) throws EosUnsupportedException, IOException;

  StatEntity getStat(DigestEntity digest) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException;

  List<String> getList(String path) throws InvalidPathException, FileNotFoundException, EosUnsupportedException;

  void move(String sourcePath, String destPath) throws InvalidPathException, FileNotFoundException, EosUnsupportedException;

  void delete(DigestEntity path) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException;

  String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException, EosUnsupportedException;

  void walk(String path, Consumer<StatEntity> consumer) throws InvalidPathException, FileNotFoundException, EosUnsupportedException;
}
