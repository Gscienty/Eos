package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.messages.DigestEntity;
import indi.eos.services.TagStore;
import indi.eos.store.StorageDriver;

@Service
public class TagStoreImpl implements TagStore {
  public List<String> getTags(StorageDriver storage) throws EosUnsupportedException, FileNotFoundException {
    List<String> tags = new LinkedList<>();
    storage.walk(this.tagsDirectory(), info -> tags.add(info.name()));
    return tags;
  }

  public DigestEntity getDigest(StorageDriver storage, String tagName) throws EosUnsupportedException, FileNotFoundException {
    try {
      return DigestEntity.toDigestEntity(new String(storage.get(this.getTagPath(tagName)), "UTF-8"));
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    } catch (IOException ex) {
      throw new FileNotFoundException();
    } catch (EosInvalidDigestException ex) {
      throw new FileNotFoundException();
    }
  }

  public void put(StorageDriver storage, String tagName, DigestEntity digest) throws EosUnsupportedException, IOException {
    storage.put(this.getTagPath(tagName), digest.getParameterValue().getBytes("UTF-8"));
  }

  private String tagsDirectory() {
    return "tags";
  }

  private String getTagPath(String tagName) {
    return String.format("%s/%s", this.tagsDirectory(), tagName);
  }
}
