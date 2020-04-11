package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.entities.RangeEntity;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.store.StorageDriver;

public interface BlobStore
{
  byte[] get(StorageDriver driver, DigestEntity digest) throws IOException, FileNotFoundException, EosUnsupportedException;

  byte[] get(StorageDriver driver, DigestEntity digest, RangeEntity range) throws FileNotFoundException, EosUnsupportedException;

  void put(StorageDriver driver, UUIDEntity uuid, InputStream inputStream, RangeEntity range) throws IOException, EosUnsupportedException;
  
  void put(StorageDriver driver, DigestEntity digest, InputStream inputStream) throws IOException, EosUnsupportedException;

  void mergePartical(StorageDriver driver, UUIDEntity uuid, DigestEntity digest) throws IOException, EosInvalidDigestException, EosUnsupportedException;

  RangeEntity getRange(StorageDriver driver, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException;

  void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;
}
