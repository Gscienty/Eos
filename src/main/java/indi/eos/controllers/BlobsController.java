package indi.eos.controllers;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.annotations.EosAuthorize;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.services.BlobStore;
import indi.eos.services.RepositoryService;

@RestController
@RequestMapping("/v2/{name}/blobs/{digest}")
public class BlobsController
{
  @Autowired
  private BlobStore blobStore;

  @Autowired
  private RepositoryService repositoryStore;

  @EosAuthorize
  @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] getAction(@PathVariable("name") String name, @PathVariable("digest") String digest)
    throws EosInvalidDigestException, FileNotFoundException, StorageDriverNotFoundException
  {
    return this.blobStore.get(this.repositoryStore.getRepository(name), DigestEntity.toDigestEntity(digest));
  }
}
