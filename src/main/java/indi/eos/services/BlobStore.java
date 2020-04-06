package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;
import indi.eos.exceptions.EosUnsupportedException;

public interface BlobStore
{
  byte[] get(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;

  void put(StorageDriver driver, byte[] content) throws IOException, EosUnsupportedException;

  void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;
}
