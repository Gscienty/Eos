package indi.eos.services;

import indi.eos.store.StorageDriver;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;

public interface RepositoryService
{
  StorageDriver getRepository(String name) throws StorageDriverNotFoundException;
}
