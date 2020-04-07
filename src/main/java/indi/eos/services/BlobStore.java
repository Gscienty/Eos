package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;

public interface BlobStore
{
  byte[] get(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;

  void put(StorageDriver driver, byte[] content) throws IOException, EosUnsupportedException;
  
  void putMono(StorageDriver driver, DigestEntity digest, InputStream inputStream) throws IOException, EosUnsupportedException;

  void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException;
}
