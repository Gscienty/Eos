package indi.eos.controllers;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.annotations.EosAuthorize;

@RestController
@RequestMapping("/v2")
public class BaseController
{
  @EosAuthorize
  @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  public String indexAction()
  {
    return "{}";
  }
}
