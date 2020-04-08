package indi.eos.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import org.springframework.util.FileCopyUtils;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;

@Service
public class BlobStoreImpl implements BlobStore {
  public byte[] get(StorageDriver driver, DigestEntity digest)
    throws FileNotFoundException, EosUnsupportedException {
    try {
      return driver.getContent(digest);
    }
    catch (EosInvalidDigestException ex) {
      throw new FileNotFoundException();
    }
  }

  public void putPartical(StorageDriver driver, UUIDEntity uuid, InputStream inputStream, long offset) throws IOException, EosUnsupportedException {
    FileCopyUtils.copy(inputStream, driver.writer(uuid, offset));
  }

  public void putMono(StorageDriver driver, DigestEntity digest, InputStream inputStream) throws IOException, EosUnsupportedException {
    try {
      FileCopyUtils.copy(inputStream, driver.writer(digest, false));
    }
    catch (EosInvalidDigestException ex) {
      throw new IOException();
    }
  }

  public void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException {
    try {
      driver.delete(digest);
    }
    catch (EosInvalidDigestException ex) {
      throw new FileNotFoundException();
    }
  }

  public long getSize(StorageDriver driver, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException {
    class tmpMaxSize {
      private long maxSize;

      public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
      }

      public long getMaxSize() {
        return this.maxSize;
      }
    }
    tmpMaxSize maxSize = new tmpMaxSize();
    driver.getStat(uuid).forEach(stat -> {
      String path = stat.getPath();
      if (!stat.getExists()) {
        return;
      }
      long offset = Long.parseLong(path.substring(path.lastIndexOf("_") + 1));
      maxSize.setMaxSize(Math.max(offset + stat.getSize(), maxSize.getMaxSize()));
    });

    return maxSize.getMaxSize();
  }
}
