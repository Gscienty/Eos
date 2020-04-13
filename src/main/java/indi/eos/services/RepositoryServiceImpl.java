package indi.eos.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.services.RepositoryService;
import indi.eos.store.StorageDriver;
import indi.eos.store.FsStorageDriver;
import indi.eos.store.MemoryStorageDriver;
import indi.eos.messages.UUIDEntity;

@Service
public class RepositoryServiceImpl implements RepositoryService {
  private Map<String, StorageDriver> drivers;
  private Map<String, StorageDriver> uploadDrivers;

  public RepositoryServiceImpl() {
    this.drivers = new HashMap<String, StorageDriver>();
    this.uploadDrivers = new HashMap<String, StorageDriver>();
    this.initDrivers();
  }

  public StorageDriver getStorage(String name, boolean upload) throws StorageDriverNotFoundException {
    StorageDriver driver = upload ? this.uploadDrivers.get(name) : this.drivers.get(name);
    if (driver == null) {
      throw new StorageDriverNotFoundException();
    }
    return driver;
  }

  public UUIDEntity createUploadStorage(String name) {
    UUIDEntity uuidEntity = UUIDEntity.generateUUID();
    if (this.uploadDrivers.get(name) == null) {
      this.uploadDrivers.put(name, new MemoryStorageDriver());
    }
    return uuidEntity;
  }

  private void initDrivers() {
    // TODO
    this.drivers.put("default", new FsStorageDriver("/root/eosfs"));
  }
}
