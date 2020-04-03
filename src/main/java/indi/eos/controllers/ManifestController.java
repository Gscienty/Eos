package indi.eos.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.messages.ManifestEntity;
import indi.eos.messages.ErrorEntity;

@RestController
@RequestMapping("/v2/{name}/manifests/{reference}")
public class ManifestController
{
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ManifestEntity getAction()
  {
    ManifestEntity manifest = new ManifestEntity();
    manifest.setSignature(new ErrorEntity());

    return manifest;
  }

  @PutMapping
  public void putAction()
  {
  }
}
