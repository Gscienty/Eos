package indi.eos.controllers;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.services.RepositoryService;

@RestController
@RequestMapping("/v2/**/blobs")
public class BlobsController
{
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
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException
  {
    return this.blobStore.get(this.repositoryStore.getRepository(this.getRepositoryName()), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @DeleteMapping(path = "/{digest}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void deleteAction(@PathVariable("digest") String digest)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException
  {
    this.blobStore.delete(this.repositoryStore.getRepository(this.getRepositoryName()), DigestEntity.toDigestEntity(digest));
  }

  @EosAuthorize
  @PostMapping(path = "/uploads/", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void initialUploadAction(HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException
  {
    String queryString = request.getQueryString();
    if (queryString != null && queryString.startsWith("digest="))
    {
      try
      {
        this.blobStore.putMono(
            this.repositoryStore.getRepository(this.getRepositoryName()),
            DigestEntity.toDigestEntity(queryString.substring(7)),
            request.getInputStream());
      }
      catch (IOException ex) { }
    }
    else
    {
      UUIDEntity uuid = UUIDEntity.generateUUID();
      try
      {
        this.blobStore.putPartical(
            this.repositoryStore.getRepository(this.getRepositoryName()),
            uuid,
            new ByteArrayInputStream(new byte[0]),
            true);
        response.setHeader("Docker-Upload-UUID", uuid.getUUID());
      }
      catch (IOException ex) { }
    }
  }

  private String getRepositoryName() throws StorageDriverNotFoundException
  {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
    String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    String name = new AntPathMatcher().extractPathWithinPattern(pattern, path);

    Matcher matcher = PATTERN.matcher(name);
    if (!matcher.find())
    {
      throw new StorageDriverNotFoundException();
    }

    return matcher.group(1);
  }
}
