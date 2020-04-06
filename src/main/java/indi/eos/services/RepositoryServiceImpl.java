package indi.eos.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.services.RepositoryService;
import indi.eos.store.StorageDriver;
import indi.eos.store.FsStorageDriver;

@Service
public class RepositoryServiceImpl implements RepositoryService
{
  private Map<String, StorageDriver> drivers;

  public RepositoryServiceImpl()
  {
    this.drivers = new HashMap<String, StorageDriver>();
    this.initDrivers();
  }

  public StorageDriver getRepository(String name) throws StorageDriverNotFoundException
  {
    StorageDriver driver = this.drivers.get(name);
    if (driver == null)
    {
      throw new StorageDriverNotFoundException();
    }
    return driver;
  }

  private void initDrivers()
  {
    // TODO
    this.drivers.put("default", new FsStorageDriver("/root/eosfs"));
  }
}
