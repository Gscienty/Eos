package indi.eos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.dao.FSStorageDetailDao;
import indi.eos.dao.RepositoryDao;
import indi.eos.entities.FsStorageDetailEntity;
import indi.eos.entities.RepositoryEntity;
import indi.eos.exceptions.EosInvalidParameterException;
import indi.eos.exceptions.RepositoryAlreadyExistException;
import indi.eos.messages.CreateRepoEntity;

@RestController
@RequestMapping(path = "/api/repo")
public class RepositoryController {

    @Autowired
    private RepositoryDao repositoryDao;

    @Autowired
    private FSStorageDetailDao fsStorageDetailDao;

    @GetMapping
    public List<RepositoryEntity> getAction() {
        return this.repositoryDao.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postAction(@RequestBody CreateRepoEntity entity) throws RepositoryAlreadyExistException, EosInvalidParameterException {
        if (this.repositoryDao.existByName(entity.getName())) {
            throw new RepositoryAlreadyExistException();
        }
        RepositoryEntity repoEntity = new RepositoryEntity();
        repoEntity.setName(entity.getName());
        repoEntity.setDriver(entity.getDriver());
        if (entity.getDriver() == null) {
            throw new EosInvalidParameterException();
        }
        switch (entity.getDriver()) {
            case "fs":
                if (entity.getFS() == null) {
                    throw new EosInvalidParameterException();
                }
                this.repositoryDao.save(repoEntity);

                FsStorageDetailEntity storage = new FsStorageDetailEntity();
                storage.setRefID(repoEntity.getID());
                storage.setRootPath(entity.getFS().getRootPath());
                this.fsStorageDetailDao.save(storage);

                return;
        }

        throw new EosInvalidParameterException();
    }
}
