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
import indi.eos.store.FileInfo;

public interface BlobStore
{
  byte[] get(StorageDriver storage, DigestEntity digest) throws IOException, FileNotFoundException, EosUnsupportedException;

  byte[] get(StorageDriver storage, DigestEntity digest, RangeEntity range) throws IOException, FileNotFoundException, EosUnsupportedException;

  void put(StorageDriver storage, UUIDEntity uuid, InputStream inputStream) throws IOException, EosUnsupportedException;

  void put(StorageDriver storage, UUIDEntity uuid, InputStream inputStream, RangeEntity range) throws IOException, EosUnsupportedException;

  DigestEntity calculateDigest(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException;

  FileInfo getInfo(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException;

  FileInfo getInfo(StorageDriver storage, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;

  void commit(StorageDriver uploadStorage, UUIDEntity uuid, StorageDriver storageDriver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;

  void delete(StorageDriver storage, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;

  void delete(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException;
}
