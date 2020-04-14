package indi.eos.services;

import indi.eos.store.StorageDriver;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.UUIDEntity;

public interface RepositoryService
{
  StorageDriver getStorage(String name, boolean upload) throws StorageDriverNotFoundException;

  UUIDEntity createUploadStorage(String name);
}
