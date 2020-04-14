package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.messages.DigestEntity;
import indi.eos.services.ManifestStore;
import indi.eos.store.StorageDriver;

@Service
public class ManifestStoreImpl implements ManifestStore {
  public void put(StorageDriver storage, DigestEntity digest, String mediaType, byte[] content) throws EosUnsupportedException, IOException {
    storage.put(this.getManifestPath(digest), content);
    storage.put(this.getMediaTypePath(digest), mediaType.getBytes("UTF-8"));
  }

  public byte[] get(StorageDriver storage, DigestEntity digest) throws EosUnsupportedException, FileNotFoundException, IOException {
    try {
      return storage.get(this.getManifestPath(digest));
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  public String getMediaType(StorageDriver storage, DigestEntity digest) throws EosUnsupportedException, FileNotFoundException, IOException {
    try {
      return new String(storage.get(this.getMediaTypePath(digest)), "UTF-8");
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  private String getManifestPath(DigestEntity digest) {
    return String.format("manifests/%s/%s/%s/data", digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex().substring(2));
  }

  private String getMediaTypePath(DigestEntity digest) {
    return String.format("manifests/%s/%s/%s/mediaType", digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex().substring(2));
  }
}
