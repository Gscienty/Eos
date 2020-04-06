package indi.eos.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.messages.DigestEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;

@Service
public class BlobStoreImpl implements BlobStore
{
  public byte[] get(StorageDriver driver, DigestEntity digest)
    throws FileNotFoundException, EosUnsupportedException
  {
    try
    {
      return driver.getContent(digest);
    }
    catch (EosInvalidDigestException ex)
    {
      throw new FileNotFoundException();
    }
  }

  public void put(StorageDriver driver, byte[] content) throws IOException, EosUnsupportedException
  {
    DigestEntity digest = new DigestEntity();
    digest.setAlgorithm("sha256");
    try
    {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      sha256.update(content);
      byte[] hash = sha256.digest();
      StringBuilder sb = new StringBuilder();
      for (byte b : hash)
      {
        sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
      }
      digest.setHex(sb.toString());
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new IOException();
    }

    try
    {
      driver.putContent(digest, content);
    }
    catch (Exception ex)
    {
      throw new IOException();
    }
  }

  public void delete(StorageDriver driver, DigestEntity digest) throws FileNotFoundException, EosUnsupportedException
  {
    try
    {
      driver.delete(digest);
    }
    catch (EosInvalidDigestException ex)
    {
      throw new FileNotFoundException();
    }
  }
}
