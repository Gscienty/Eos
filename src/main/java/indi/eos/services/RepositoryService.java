package indi.eos.services;

import indi.eos.store.StorageDriver;
import indi.eos.exceptions.StorageDriverNotFoundException;

public interface RepositoryService
{
  StorageDriver getRepository(String name) throws StorageDriverNotFoundException;
}
