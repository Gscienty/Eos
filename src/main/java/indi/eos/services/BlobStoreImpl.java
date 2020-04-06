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
import indi.eos.services.BlobStore;
import indi.eos.store.StorageDriver;

@Service
public class BlobStoreImpl implements BlobStore
{
  public byte[] get(StorageDriver driver, DigestEntity digest)
    throws FileNotFoundException
  {
    try
    {
      return driver.getContent(this.digestToPath(digest));
    }
    catch (InvalidPathException ex)
    {
      throw new FileNotFoundException();
    }
  }

  public void put(StorageDriver driver, byte[] content) throws IOException
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

    String path = this.digestToPath(digest);

    try
    {
      driver.getStat(path);
      throw new IOException();
    }
    catch (FileNotFoundException ex) { }
    catch (InvalidPathException ex)
    {
      throw new IOException();
    }

    try
    {
      driver.putContent(path, content);
    }
    catch (Exception ex)
    {
      throw new IOException();
    }
  }

  private String digestToPath(DigestEntity digest)
  {
    return String.format("/blobs/%s/%s/%s/data",
        digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex());
  }
}
