package indi.eos.services;

import java.io.FileNotFoundException;
import java.util.List;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.store.StorageDriver;

public interface TagStore {
  List<String> getTags(StorageDriver storage) throws EosUnsupportedException, FileNotFoundException;
}
