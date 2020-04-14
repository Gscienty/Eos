package indi.eos.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;


import indi.eos.entities.RangeEntity;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.messages.DigestEntity;
import indi.eos.messages.UUIDEntity;
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;
import indi.eos.store.FileInfo;

@Service
public class BlobStoreImpl implements BlobStore {
  public byte[] get(StorageDriver storage, DigestEntity digest)
    throws IOException, FileNotFoundException, EosUnsupportedException {
    try {
      return storage.get(this.digestToPath(digest));
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  public byte[] get(StorageDriver storage, DigestEntity digest, RangeEntity range)
    throws IOException, FileNotFoundException, EosUnsupportedException {
    byte[] buffer = new byte[(int) (range.getEnd() - range.getStart())];
    try {
      InputStream input = storage.reader(this.digestToPath(digest), range.getStart());
      input.read(buffer);
      return buffer;
    } catch (InvalidOffsetException ex) {
      throw new IOException();
    }
  }

  public void put(StorageDriver storage, UUIDEntity uuid, InputStream inputStream) throws IOException, EosUnsupportedException {
    this.copyTo(inputStream, storage.writer(this.uuidToPath(uuid), true));
  }

  public void put(StorageDriver storage, UUIDEntity uuid, InputStream inputStream, RangeEntity range) throws IOException, EosUnsupportedException {
    this.copyTo(inputStream, storage.writer(this.uuidToPath(uuid), range.getStart()));
  }

  public DigestEntity calculateDigest(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException {
    try {
      return DigestEntity.toDigestEntity(storage.reader(this.uuidToPath(uuid), 0));
    } catch (IOException ex) {
      throw new FileNotFoundException();
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  public void commit(StorageDriver uploadDriver, UUIDEntity uuid, StorageDriver storageDriver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException {
    String uploadPath = this.uuidToPath(uuid);
    try {
      InputStream input = uploadDriver.reader(uploadPath, 0);
      OutputStream output = storageDriver.writer(this.digestToPath(digest), false);
      byte[] buffer = new byte[4096];
      int len = -1;
      while ((len = input.read(buffer)) != -1) {
        output.write(buffer);
      }
      output.close();
      input.close();

      uploadDriver.delete(uploadPath);
    } catch (IOException ex) {
      throw new FileNotFoundException();
    } catch (InvalidOffsetException ex) {
      throw new FileNotFoundException();
    }
  }

  public FileInfo getInfo(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException {
    return storage.getInfo(this.uuidToPath(uuid));
  }

  public FileInfo getInfo(StorageDriver storage, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException {
    return storage.getInfo(this.digestToPath(digest));
  }

  public void delete(StorageDriver storage, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException {
    storage.delete(this.digestToPath(digest));
  }

  public void delete(StorageDriver storage, UUIDEntity uuid) throws FileNotFoundException, EosUnsupportedException {
    storage.delete(this.uuidToPath(uuid));
  }

  private String digestToPath(DigestEntity digest) {
    return String.format("blobs/%s/%s/%s/data", digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex().substring(2));
  }

  private String uuidToPath(UUIDEntity uuid) {
    return String.format("_uploads/%s/data", uuid.getUUID());
  }

  private void copyTo(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int len = -1;
    while ((len = input.read(buffer)) != -1) {
      output.write(buffer, 0, len);
    }
    output.close();
    input.close();
  }
}
