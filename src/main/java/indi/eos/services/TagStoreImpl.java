package indi.eos.services;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.services.TagStore;
import indi.eos.store.StorageDriver;

@Service
public class TagStoreImpl implements TagStore {
  public List<String> getTags(StorageDriver storage) throws EosUnsupportedException, FileNotFoundException {
    List<String> tags = new LinkedList<>();
    storage.walk(this.tagsDirectory(), info -> tags.add(info.name()));
    return tags;
  }

  private String tagsDirectory() {
    return "tags";
  }
}
