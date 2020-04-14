package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;

public interface ManifestStore {
  void put(StorageDriver storage, DigestEntity digest, String mediaType, byte[] content) throws EosUnsupportedException, IOException;

  byte[] get(StorageDriver storage, DigestEntity digest) throws EosUnsupportedException, FileNotFoundException, IOException;

  String getMediaType(StorageDriver storage, DigestEntity digest) throws EosUnsupportedException, FileNotFoundException, IOException;
}
