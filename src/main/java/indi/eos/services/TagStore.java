package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;

public interface TagStore {
  List<String> getTags(StorageDriver storage) throws EosUnsupportedException, FileNotFoundException;

  DigestEntity getDigest(StorageDriver storage, String tagName) throws EosUnsupportedException, FileNotFoundException;

  void put(StorageDriver storage, String tagName, DigestEntity digest) throws EosUnsupportedException, IOException;
}
