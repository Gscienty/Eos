package indi.eos.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.messages.ErrorEntity;
import indi.eos.messages.ManifestEntity;
import indi.eos.annotations.EosAuthenticate;

@RestController
@RequestMapping("/v2/{name}/manifests/{reference}")
public class ManifestController
{
  @EosAuthenticate
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ManifestEntity getAction()
  {
    ManifestEntity manifest = new ManifestEntity();

    return manifest;
  }

  @EosAuthenticate
  @PutMapping
  public HttpHeaders putAction(@RequestBody ManifestEntity entity)
  {
    HttpHeaders headers = new HttpHeaders();

    return headers;
  }
}
