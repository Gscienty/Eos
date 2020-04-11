package indi.eos.controllers;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.web.bind.annotation.PutMapping;
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
import indi.eos.entities.StatEntity;
import indi.eos.entities.RangeEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosInvalidParameterException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.services.RepositoryService;
import indi.eos.store.StorageDriver;

@RestController
@RequestMapping("/v2/**/blobs")
public class BlobsController {
  private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("^(.*)/blobs/.*$");
  private static final Pattern RANGE_PATTERN = Pattern.compile("^\\d+-\\d+$");

  @Autowired
  private BlobStore blobStore;

  @Autowired
  private RepositoryService repositoryStore;

  @EosAuthorize
  @GetMapping(path = "/{digest}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public byte[] getAction(
      @PathVariable("digest") String digest,
      @RequestHeader(name = "Range", required = false) String range,
      HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, EosInvalidParameterException {

    throw new EosUnsupportedException();
  }

  @EosAuthorize
  @DeleteMapping(path = "/{digest}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteAction(@PathVariable("digest") String digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  @EosAuthorize
  @PostMapping(path = "/uploads/", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void initialUploadAction(HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  @EosAuthorize
  @GetMapping(path = "/uploads/{uuid}")
  public void getBlobUploadAction(@PathVariable("uuid") String uuid, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  @EosAuthorize
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping(path = "/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void patchBlobUploadAction(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, EosInvalidParameterException {
    throw new EosUnsupportedException();
  }

  @EosAuthorize
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(path = "/uploads/{uuid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void putBlobUploadAction(
      @PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  private void uploadChunkAction(InputStream inputStream, UUIDEntity uuid, RangeEntity range)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, IOException {
    throw new EosUnsupportedException();
  }

  private void initialResumeUploadAction(HttpServletResponse response, UUIDEntity uuid)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  private void initialMonoUploadAction(HttpServletRequest request, HttpServletResponse response, DigestEntity digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    throw new EosUnsupportedException();
  }

  private String getRepositoryName() throws StorageDriverNotFoundException {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
    String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    String name = new AntPathMatcher().extractPathWithinPattern(pattern, path);

    Matcher matcher = REPOSITORY_NAME_PATTERN.matcher(name);
    if (!matcher.find()) {
      throw new StorageDriverNotFoundException();
    }

    return matcher.group(1);
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
