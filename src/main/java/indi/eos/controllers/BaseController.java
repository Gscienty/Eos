package indi.eos.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.messages.JWTEntity;
import indi.eos.messages.JWTHeaderEntity;
import indi.eos.messages.JWTPayloadEntity;
import indi.eos.services.JWTService;

@RestController
@RequestMapping("/v2")
public class BaseController
{
  @Autowired
  private JWTService<JWTPayloadEntity> jwtService;

  @GetMapping(path = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  public String index()
  {
    return "The API implements V2 protocol and is accessible.";
  }
}
