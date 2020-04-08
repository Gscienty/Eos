package indi.eos.controllers;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import org.springframework.util.AntPathMatcher;

import org.springframework.web.servlet.HandlerMapping;

import indi.eos.annotations.EosAuthorize;
import indi.eos.annotations.EosResponseDigest;
import indi.eos.entities.StatEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.services.RepositoryService;

@RestController
@RequestMapping("/v2/**/blobs")
public class BlobsController {
  private static final Pattern PATTERN = Pattern.compile("^(.*)/blobs/.*$");

  @Autowired
  private BlobStore blobStore;

  @Autowired
  private RepositoryService repositoryStore;

  @EosAuthorize
  @EosResponseDigest
  @GetMapping(path = "/{digest}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] getAction(
      @PathVariable("digest") String digest,
      @RequestHeader(name = "Range", required = false) String range)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    return this.blobStore.get(this.repositoryStore.getRepository(this.getRepositoryName()), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @DeleteMapping(path = "/{digest}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteAction(@PathVariable("digest") String digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    this.blobStore.delete(this.repositoryStore.getRepository(this.getRepositoryName()), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @PostMapping(path = "/uploads/", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void initialUploadAction(HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    UUIDEntity uuid = UUIDEntity.generateUUID();
    response.setHeader("Docker-Upload-UUID", uuid.getUUID());

    Map<String, String> query = this.getQuery();
    if (query != null && query.get("digest") != null) {
      this.initialMonoUploadAction(request, response, DigestEntity.toDigestEntity(query.get("digest")));
    }
    else if (query != null && query.get("mount") != null && query.get("from") != null) {
      throw new EosUnsupportedException();
    }
    else {
      this.initialResumeUploadAction(response, uuid);
    }
  }

  @EosAuthorize
  @GetMapping(path = "/uploads/{uuid}")
  public void getBlobUploadAction(@PathVariable("uuid") String uuid, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    UUIDEntity uuidEntity = new UUIDEntity();
    uuidEntity.setUUID(uuid);

    response.setHeader("Docker-Upload-UUID", uuidEntity.getUUID());
    response.setHeader("Range", String.format("0-%d",
          this.blobStore.getSize(this.repositoryStore.getRepository(this.getRepositoryName()), uuidEntity)));
  }

  @EosAuthorize
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping(path = "/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void patchBlobUploadAction(
      @PathVariable("uuid") String uuid,
      HttpServletRequest request,
      HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    UUIDEntity uuidEntity = new UUIDEntity();
    uuidEntity.setUUID(uuid);
    String range = request.getHeader("Content-Range");
    response.setHeader("Docker-Upload-UUID", uuidEntity.getUUID());

    if (range == null) {
      try {
        this.blobStore.putPartical(
            this.repositoryStore.getRepository(this.getRepositoryName()),
            uuidEntity,
            request.getInputStream(),
            0); 
      }
      catch (IOException ex) { } // TODO 400
    }
    else {
      try {
        range = range.split("-")[0];
        this.blobStore.putPartical(
            this.repositoryStore.getRepository(this.getRepositoryName()),
            uuidEntity,
            request.getInputStream(),
            Long.parseLong(range)); 
      }
      catch (IOException ex) { } // TODO 400
    }
    response.setHeader("Range", String.format("0-%d",
          this.blobStore.getSize(this.repositoryStore.getRepository(this.getRepositoryName()), uuidEntity)));
  }

  private void initialResumeUploadAction(HttpServletResponse response, UUIDEntity uuid)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    try {
      String repositoryName = this.getRepositoryName();
      this.blobStore.putPartical(
          this.repositoryStore.getRepository(repositoryName),
          uuid,
          new ByteArrayInputStream(new byte[0]),
          0);
      response.setHeader("Range", "0-0");
      response.setHeader("Location", String.format("/v2/%s/blobs/uploads/%s", repositoryName, uuid.getUUID()));
        response.setStatus(HttpStatus.ACCEPTED.value());
    }
    catch (IOException ex) { }
  }

  private void initialMonoUploadAction(HttpServletRequest request, HttpServletResponse response, DigestEntity digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
      try {
        this.blobStore.putMono( this.repositoryStore.getRepository(this.getRepositoryName()), digest, request.getInputStream());
        response.setStatus(HttpStatus.CREATED.value());
      }
      catch (IOException ex) { }
  }

  private String getRepositoryName() throws StorageDriverNotFoundException {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
    String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    String name = new AntPathMatcher().extractPathWithinPattern(pattern, path);

    Matcher matcher = PATTERN.matcher(name);
    if (!matcher.find()) {
      throw new StorageDriverNotFoundException();
    }

    return matcher.group(1);
  }

  private Map<String, String> getQuery() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    String queryString = request.getQueryString();

    if (queryString == null) {
      return null;
    }

    Map<String, String> query = new HashMap<>();
    for (String item : queryString.split("&")) {
      int position = item.indexOf("=");
      if (position == -1) {
        continue;
      }
      query.put(item.substring(0, position), item.substring(position + 1));
    }

    return query;
  }
}
