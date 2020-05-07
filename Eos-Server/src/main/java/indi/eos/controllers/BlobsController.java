package indi.eos.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.entities.RangeEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosInvalidParameterException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;
import indi.eos.store.FileInfo;
import indi.eos.annocations.EosAuthorize;
import indi.eos.controllers.RegistryBaseController;

@RestController
@RequestMapping("/v2/**/blobs")
public class BlobsController extends RegistryBaseController {
  private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("^(.*)/blobs/.*$");
  private static final Pattern RANGE_PATTERN = Pattern.compile("^\\d+-\\d+$");

  @Override
  protected Pattern getRepositoryNamePattern() {
    return REPOSITORY_NAME_PATTERN;
  }

  @Autowired
  private BlobStore blobStore;


  @EosAuthorize
  @GetMapping(path = "/{digest}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] getAction(
      @PathVariable("digest") String digest,
      @RequestHeader(name = "Range", required = false) String range,
      HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, EosInvalidParameterException {
    try {
      if (range == null) {
        return this.blobStore.get(this.getStorage(false), DigestEntity.toDigestEntity(digest));
      } else if (range.startsWith("bytes=")) {
        range = range.substring(7);
        return this.blobStore.get(this.getStorage(false), DigestEntity.toDigestEntity(digest), this.getRange(range, false));
      } else {
        throw new EosUnsupportedException();
      }
    } catch (IOException ex) {
      throw new FileNotFoundException();
    }
  }

  @EosAuthorize
  @DeleteMapping(path = "/{digest}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteAction(@PathVariable("digest") String digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    this.blobStore.delete(this.getStorage(false), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @DeleteMapping(path = "/uploads/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUploadAction(@PathVariable("uuid") String uuid)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    this.blobStore.delete(this.getStorage(true), UUIDEntity.toUUIDEntity(uuid));
  }

  @EosAuthorize
  @PostMapping(path = "/uploads/", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void initialUploadAction(HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    Map<String, String> query = this.getQuery();
    String from = query.get("from");
    String mount = query.get("mount");
    String digest = query.get("digest");
    if (from != null && mount != null) {
      throw new EosUnsupportedException();
    } else if (digest != null) {
      this.initialMonoUploadAction(request, response, DigestEntity.toDigestEntity(digest));
    } else {
      this.initialResumeUploadAction(response);
    }
  }

  @EosAuthorize
  @GetMapping(path = "/uploads/{uuid}")
  public void getBlobUploadAction(@PathVariable("uuid") String uuid, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    UUIDEntity uuidEntity = UUIDEntity.toUUIDEntity(uuid);
    FileInfo info = this.blobStore.getInfo(this.getStorage(true), uuidEntity);
    RangeEntity range = new RangeEntity();
    range.setEnd(info.size());

    response.setStatus(HttpStatus.NO_CONTENT.value());
    response.setHeader("Range", range.getParameterValue());
    response.setHeader("Docker-Upload-UUID", uuidEntity.getUUID());
  }

  @EosAuthorize
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping(path = "/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void patchBlobUploadAction(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, EosInvalidParameterException {
    UUIDEntity uuidEntity = UUIDEntity.toUUIDEntity(uuid);
    String repositoryName = this.getRepositoryName();
    StorageDriver storage = this.getStorage(repositoryName, true);

    try {
      String range = request.getHeader("Content-Range");
      if (range == null) {
        this.blobStore.put(storage, uuidEntity, request.getInputStream());
      } else {
        this.blobStore.put(storage, uuidEntity, request.getInputStream(), this.getRange(range, true));
      }
    } catch (IOException ex) { }

    FileInfo info = this.blobStore.getInfo(storage, uuidEntity);
    RangeEntity resultRange = new RangeEntity();
    resultRange.setEnd(info.size());

    response.setStatus(HttpStatus.NO_CONTENT.value());
    response.setHeader("Location", String.format("/v2/%s/blobs/uploads/%s", repositoryName, uuidEntity.getUUID()));
    response.setHeader("Range", resultRange.getParameterValue());
    response.setHeader("Docker-Upload-UUID", uuidEntity.getUUID());
  }

  @EosAuthorize
  @PutMapping(path = "/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void putBlobUploadAction(
      @PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    Map<String, String> query = this.getQuery();
    String digest = query.get("digest");
    if (digest == null) {
      throw new EosInvalidDigestException();
    }
    String repositoryName = this.getRepositoryName();
    StorageDriver uploadStorage = this.getStorage(repositoryName, true);
    StorageDriver commitedStorage = this.getStorage(repositoryName, false);
    UUIDEntity uuidEntity = UUIDEntity.toUUIDEntity(uuid);
    DigestEntity digestEntity = DigestEntity.toDigestEntity(digest);

    FileInfo uploadInfo = this.blobStore.getInfo(uploadStorage, uuidEntity);
    RangeEntity resultRange = new RangeEntity();
    resultRange.setStart(uploadInfo.size());
    try {
      this.blobStore.put(uploadStorage, uuidEntity, request.getInputStream());
    } catch (IOException ex) { }
    resultRange.setEnd(uploadInfo.size() - 1);

    DigestEntity calculatedDigest = this.blobStore.calculateDigest(uploadStorage, uuidEntity);
    if (!digestEntity.equals(calculatedDigest)) {
      // TODO
    }
    this.blobStore.commit(uploadStorage, uuidEntity, commitedStorage, calculatedDigest);

    response.setStatus(HttpStatus.CREATED.value());
    response.setHeader("Location", String.format("/v2/%s/blobs/%s:%s", repositoryName, calculatedDigest.getAlgorithm(), calculatedDigest.getHex()));
    response.setHeader("Docker-Content-Digest", calculatedDigest.getParameterValue());
  }

  private void initialResumeUploadAction(HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    String repositoryName = this.getRepositoryName();
    UUIDEntity uuid = this.repositoryStore.createUploadStorage(repositoryName);

    response.setStatus(HttpStatus.ACCEPTED.value());
    response.setHeader("Location", String.format("/v2/%s/blobs/uploads/%s", repositoryName, uuid.getUUID()));
    response.setHeader("Range", "0-0");
    response.setHeader("Docker-Upload-UUID", uuid.getUUID());
  }

  private void initialMonoUploadAction(HttpServletRequest request, HttpServletResponse response, DigestEntity digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    String repositoryName = this.getRepositoryName();
    UUIDEntity uuid = this.repositoryStore.createUploadStorage(repositoryName);
    StorageDriver uploadStorage = this.getStorage(repositoryName, true);
    try {
      this.blobStore.put(uploadStorage, uuid, request.getInputStream());
      DigestEntity calculatedDigest = this.blobStore.calculateDigest(uploadStorage, uuid);
      if (!digest.equals(calculatedDigest)) { }
      this.blobStore.commit(uploadStorage, uuid, this.getStorage(repositoryName, false), calculatedDigest);

      response.setStatus(HttpStatus.CREATED.value());
      response.setHeader("Location", String.format("/v2/%s/blobs/%s:%s", repositoryName, calculatedDigest.getAlgorithm(), calculatedDigest.getHex()));
      response.setHeader("Docker-Upload-UUID", uuid.getUUID());
    } catch (IOException ex) { }
  }

  private RangeEntity getRange(String range, boolean inclusive) throws EosInvalidParameterException {
    Matcher matcher = RANGE_PATTERN.matcher(range);
    if (!matcher.find()) {
      throw new EosInvalidParameterException();
    }
    RangeEntity rangeEntity = new RangeEntity();
    rangeEntity.setStart(Long.parseLong(matcher.group(1)));
    rangeEntity.setEnd(Long.parseLong(matcher.group(2)) + (inclusive ? 1 : 0));
    return rangeEntity;
  }

}
