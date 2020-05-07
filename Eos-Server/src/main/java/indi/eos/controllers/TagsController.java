package indi.eos.controllers;

import java.io.FileNotFoundException;
import java.util.Map;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.eos.annocations.EosAuthorize;
import indi.eos.controllers.RegistryBaseController;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.TagsEntity;
import indi.eos.services.TagStore;

@RestController
@RequestMapping("/v2/**/tags")
public class TagsController extends RegistryBaseController {
  private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("^(.*)/tags/.*$");

  @Autowired
  private TagStore tagStore;

  @Override
  protected Pattern getRepositoryNamePattern() {
    return REPOSITORY_NAME_PATTERN;
  }

  @EosAuthorize
  @GetMapping("/list")
  public TagsEntity getAction()
    throws EosUnsupportedException, FileNotFoundException, StorageDriverNotFoundException {
    String repositoryName = this.getRepositoryName();
    TagsEntity result = new TagsEntity();
    result.setName(repositoryName);
    result.setTags(this.tagStore.getTags(this.getStorage(repositoryName, false)));

    Map<String, String> query = this.getQuery();
    String n = query.get("n");
    String last = query.get("last");
    if (n != null && last != null) {
      result.setTags(result.getTags().subList(Integer.parseInt(n), Integer.parseInt(last)));
    }
    return result;
  }
}
