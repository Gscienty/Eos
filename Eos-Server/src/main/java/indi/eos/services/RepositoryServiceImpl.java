package indi.eos.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import indi.eos.dao.FSStorageDetailDao;
import indi.eos.dao.RepositoryDao;
import indi.eos.entities.FsStorageDetailEntity;
import indi.eos.entities.RepositoryEntity;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.services.RepositoryService;
import indi.eos.store.StorageDriver;
import indi.eos.store.FsStorageDriver;
import indi.eos.store.MemoryStorageDriver;
import indi.eos.messages.UUIDEntity;

@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Autowired
    private RepositoryDao repositoryDao;
    @Autowired
    private FSStorageDetailDao fsStorageDetailDao;
    private Map<String, StorageDriver> uploadDrivers;

    public RepositoryServiceImpl() {
        this.uploadDrivers = new HashMap<String, StorageDriver>();
    }

    public StorageDriver getStorage(String name, boolean upload) throws StorageDriverNotFoundException {
        if (upload) {
            return this.uploadDrivers.get(name);
        }
        RepositoryEntity entity = this.repositoryDao.findOneByName(name);
        if (entity == null) {
            throw new StorageDriverNotFoundException();
        }
        switch (entity.getDriver()) {
            case "fs":
                FsStorageDetailEntity detail = this.fsStorageDetailDao.findOneByRefID(entity.getID());
                if (detail == null) {
                    throw new StorageDriverNotFoundException();
                }
                return new FsStorageDriver(detail.getRootPath());

            default:
                throw new StorageDriverNotFoundException();
        }
    }

    public UUIDEntity createUploadStorage(String name) {
        UUIDEntity uuidEntity = UUIDEntity.generateUUID();
        if (this.uploadDrivers.get(name) == null) {
            this.uploadDrivers.put(name, new MemoryStorageDriver());
        }
        return uuidEntity;
    }
}
