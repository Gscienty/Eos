package indi.eos.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.annotations.EosAuthorize;
import indi.eos.controllers.RegistryBaseController;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosInvalidParameterException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.DigestEntity;
import indi.eos.services.ManifestStore;
import indi.eos.services.TagStore;
import indi.eos.store.StorageDriver;

@RestController
@RequestMapping("/v2/**/manifests/{reference}")
public class ManifestsController extends RegistryBaseController {
  private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("^(.*)/manifests/.*$");

  @Autowired
  private ManifestStore manifestStore;

  @Autowired
  private TagStore tagStore;

  @Override
  protected Pattern getRepositoryNamePattern() {
    return REPOSITORY_NAME_PATTERN;
  }

  @EosAuthorize
  @GetMapping
  public byte[] getAction(@PathVariable(name = "reference") String reference, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    StorageDriver storage = this.getStorage(false);
    DigestEntity digest; 
    if (DigestEntity.isDigest(reference)) {
      digest = DigestEntity.toDigestEntity(reference);
    } else {
      digest = this.tagStore.getDigest(storage, reference);
    }

    response.setHeader("Content-Type", this.manifestStore.getMediaType(storage, digest));
    response.setHeader("Docker-Content-Digest", digest.getParameterValue());
    return this.manifestStore.get(storage, digest);
  }

  @EosAuthorize
  @PutMapping
  public void putAction(
      @PathVariable(name = "reference") String reference, HttpServletRequest request, HttpServletResponse response)
    throws EosInvalidDigestException, EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException, EosInvalidParameterException {
    try {
      InputStream input;
      input = request.getInputStream();
      StorageDriver storage = this.getStorage(false);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int len = -1;
      while ((len = input.read(buffer)) != -1) {
        output.write(buffer, 0, len);
      }
      buffer = output.toByteArray();

      DigestEntity digest; 
      if (DigestEntity.isDigest(reference)) {
        digest = DigestEntity.toDigestEntity(reference);
        JSONObject obj = new JSONObject(new String(buffer, "UTF-8"));
        this.tagStore.put(storage, obj.getString("tag"), digest);
      } else {
        digest = DigestEntity.toDigestEntity(new ByteArrayInputStream(buffer));
        this.tagStore.put(storage, reference, digest);
      }
      this.manifestStore.put(storage, digest, buffer);

      response.setStatus(HttpStatus.CREATED.value());
      response.setHeader("Location", String.format("/v2/%s/manifests/%s", this.getRepositoryName(), digest.getParameterValue()));
      response.setHeader("Docker-Content-Digest", digest.getParameterValue());
    } catch (IOException ex) {
      throw new EosUnsupportedException();
    } catch (JSONException ex) {
      throw new EosInvalidParameterException();
    }
  }
}
