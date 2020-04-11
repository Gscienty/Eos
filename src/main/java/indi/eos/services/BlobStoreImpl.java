package indi.eos.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Stack;

import org.springframework.stereotype.Service;

import org.springframework.util.FileCopyUtils;

import indi.eos.entities.StatEntity;
import indi.eos.entities.RangeEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;

@Service
public class BlobStoreImpl implements BlobStore {
  public byte[] get(StorageDriver driver, DigestEntity digest)
    throws IOException, FileNotFoundException, EosUnsupportedException {
    try {
      return driver.get(this.digestToPath(digest));
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  public byte[] get(StorageDriver driver, DigestEntity digest, RangeEntity range)
    throws FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void put(StorageDriver driver, UUIDEntity uuid, InputStream inputStream, RangeEntity range) throws IOException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void put(StorageDriver driver, DigestEntity digest, InputStream inputStream) throws IOException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void mergePartical(StorageDriver driver, UUIDEntity uuid, DigestEntity digest) throws IOException, EosInvalidDigestException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  public RangeEntity getRange(StorageDriver driver, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException {
    throw new EosUnsupportedException();
  }

  private String digestToPath(DigestEntity digest) {
    return String.format("blobs/%s/%s/%s/data", digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex().substring(2));
  }
}
