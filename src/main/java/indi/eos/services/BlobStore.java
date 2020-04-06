package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;

public interface BlobStore
{
  byte[] get(StorageDriver driver, DigestEntity digest) throws FileNotFoundException;

  void put(StorageDriver driver, byte[] content) throws IOException;
}
