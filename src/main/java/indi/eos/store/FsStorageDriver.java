package indi.eos.store;

import java.util.List;
import java.util.function.Consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.messages.DigestEntity;
import indi.eos.store.StorageDriver;

public class FsStorageDriver implements StorageDriver
{
  private static final String DEFAULT_ROOT_DIRECTORY = "/var/lib/registry";
  private final File rootDirectory;

  public FsStorageDriver(String rootDirectory)
  {
    this.rootDirectory = new File(rootDirectory);
  }

  public FsStorageDriver()
  {
    this.rootDirectory = new File(DEFAULT_ROOT_DIRECTORY);
  }

  public String getName()
  {
    return "filesystem";
  }

  public byte[] getContent(DigestEntity digest) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException
  {
    StatEntity stat = this.getStat(digest);
    byte[] buf = new byte[stat.getSize().intValue()];
    try
    {
      InputStream input = this.reader(digest, 0);
      input.read(buf);
      input.close();
      return buf;
    }
    catch (InvalidOffsetException ex)
    {
      return new byte[0];
    }
    catch (IOException ex)
    {
      throw new FileNotFoundException();
    }
  }

  public void putContent(DigestEntity digest, byte[] content) throws EosInvalidDigestException, FileNotFoundException, EosInvalidDigestException, EosUnsupportedException, IOException
  {
    OutputStream output = this.writer(digest, false);
    output.write(content);
    output.close();
  }
  
  public InputStream reader(DigestEntity digest, long offset) throws EosInvalidDigestException, FileNotFoundException, InvalidOffsetException, EosUnsupportedException
  {
    String path = this.digestToPath(digest);
    File file = new File(this.rootDirectory, path);
    if (!file.exists() || !file.isFile())
    {
      throw new FileNotFoundException();
    }
    try
    {
      InputStream inputStream = new FileInputStream(file);
      inputStream.skip(offset);
      return inputStream;
    }
    catch (IOException ex)
    {
      throw new InvalidOffsetException(this.getName(), path, offset);
    }
  }

  public OutputStream writer(DigestEntity digest, boolean append) throws EosInvalidDigestException, EosUnsupportedException, IOException
  {
    String path = this.digestToPath(digest);
    File file = new File(this.rootDirectory, path);
    if (!file.exists())
    {
      if (!file.getParentFile().exists())
      {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
    return new FileOutputStream(file, append);
  }

  public StatEntity getStat(DigestEntity digest) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException
  {
    File file = new File(this.rootDirectory, this.digestToPath(digest));
    StatEntity stat = new StatEntity();
    stat.setPath(file.getPath());
    stat.setTime(file.lastModified());
    stat.setSize(file.length());
    stat.setIsDirectory(file.isDirectory());

    return stat;
  }

  public List<String> getList(String path) throws InvalidPathException, FileNotFoundException, EosUnsupportedException
  {
    File directory = new File(this.rootDirectory, path);

    File[] files = directory.listFiles();
    List<String> result = new ArrayList<>(files.length);
    for (int i = 0; i < files.length; i++)
    {
      result.set(i, files[i].getPath());
    }

    return result;
  }

  public void move(String sourcePath, String destPath) throws InvalidPathException, FileNotFoundException, EosUnsupportedException
  {
    File sourceFile = new File(this.rootDirectory, sourcePath);
    File destFile = new File(this.rootDirectory, destPath);
    if (!sourceFile.exists())
    {
      throw new FileNotFoundException();
    }
    destFile.mkdirs();
    sourceFile.renameTo(new File(this.rootDirectory, destFile.getParent()));
  }

  public void delete(DigestEntity digest) throws EosInvalidDigestException, FileNotFoundException, EosUnsupportedException
  {
    File file = new File(this.rootDirectory, this.digestToPath(digest));
    if (!file.exists())
    {
      throw new FileNotFoundException();
    }
    file.delete();
  }

  public String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException, EosUnsupportedException
  {
    throw new EosUnsupportedException();
  }

  public void walk(String path, Consumer<StatEntity> consumer) throws InvalidPathException, FileNotFoundException, EosUnsupportedException
  {
    throw new EosUnsupportedException();
  }

  private String digestToPath(DigestEntity digest)
  {
    return String.format("/blobs/%s/%s/%s/data",
        digest.getAlgorithm(), digest.getHex().substring(0, 2), digest.getHex());
  }
}
