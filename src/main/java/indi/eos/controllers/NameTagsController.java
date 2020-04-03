package indi.eos.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.messages.TagsEntity;
import indi.eos.annotations.EosAuthenticate;

@RestController
@RequestMapping("/v2/{name}/tags")
public class NameTagsController
{
  @EosAuthenticate
  @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE) 
  public TagsEntity listAction(
      @PathVariable("name") String name,
      @RequestParam(name = "n", required = false) Integer n,
      @RequestParam(name = "last", required = false) Integer last)
  {
    TagsEntity tags = new TagsEntity();
    tags.setName(name);

    return tags;
  }
}
