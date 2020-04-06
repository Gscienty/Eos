package indi.eos.controllers;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.annotations.EosAuthorize;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
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
  public byte[] getAction(
      @PathVariable("name") String name,
      @PathVariable("digest") String digest,
      @RequestHeader(name = "Range", required = false) String range)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException
  {
    return this.blobStore.get(this.repositoryStore.getRepository(name), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @DeleteMapping
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteAction(@PathVariable("name") String name, @PathVariable("digest") String digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException
  {
    this.blobStore.delete(this.repositoryStore.getRepository(name), DigestEntity.toDigestEntity(digest));
  }
}
