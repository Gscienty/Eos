package indi.eos.controllers;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.AntPathMatcher;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.web.servlet.HandlerMapping;

import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.services.RepositoryService;
import indi.eos.store.StorageDriver;

public abstract class RegistryBaseController {
  @Autowired
  protected RepositoryService repositoryStore;

  protected abstract Pattern getRepositoryNamePattern();

  protected String getRepositoryName() throws StorageDriverNotFoundException {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
    String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    String name = new AntPathMatcher().extractPathWithinPattern(pattern, path);

    Matcher matcher = this.getRepositoryNamePattern().matcher(name);
    if (!matcher.find()) {
      throw new StorageDriverNotFoundException();
    }

    return matcher.group(1);
  }

  protected StorageDriver getStorage(boolean upload) throws StorageDriverNotFoundException {
    return this.repositoryStore.getStorage(this.getRepositoryName(), upload);
  }

  protected StorageDriver getStorage(String repositoryName, boolean upload) throws StorageDriverNotFoundException {
    return this.repositoryStore.getStorage(repositoryName, upload);
  }

  protected Map<String, String> getQuery() {
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
