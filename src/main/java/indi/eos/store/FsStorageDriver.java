package indi.eos.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import java.util.function.Consumer;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.store.BaseStorageDriver;

public class FsStorageDriver extends BaseStorageDriver
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

  @Override
  public String getName()
  {
    return "filesystem";
  }

  @Override
  protected byte[] getContentImplement(String path) throws FileNotFoundException
  {
    StatEntity stat = this.getStatImplement(path);
    byte[] buf = new byte[stat.getSize().intValue()];
    try
    {
      InputStream input = this.readerImplement(path, 0);
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

  @Override
  protected void putContentImplement(String path, byte[] content) throws FileNotFoundException, IOException
  {
    OutputStream output = this.writerImplement(path, false);
    try
    {
      output.write(content);
      output.close();
    }
    catch (IOException ex) { }
  }
  
  @Override
  protected InputStream readerImplement(String path, long offset) throws FileNotFoundException, InvalidOffsetException
  {
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

  @Override
  protected OutputStream writerImplement(String path, boolean append) throws IOException
  {
    File file = new File(this.rootDirectory, path);
    if (!file.exists())
    {
      file.createNewFile();
    }
    try
    {
      return new FileOutputStream(new File(this.rootDirectory, path), append);
    }
    catch (FileNotFoundException ex)
    {
      throw new IOException();
    }
  }

  @Override
  protected StatEntity getStatImplement(String path) throws FileNotFoundException
  {
    File file = new File(this.rootDirectory, path);
    StatEntity stat = new StatEntity();
    stat.setPath(file.getPath());
    stat.setTime(file.lastModified());
    stat.setSize(file.length());
    stat.setIsDirectory(file.isDirectory());

    return stat;
  }

  @Override
  protected List<String> getListImplement(String path) throws FileNotFoundException
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

  @Override
  protected void moveImplement(String sourcePath, String destPath) throws FileNotFoundException
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

  @Override
  protected void deleteImplement(String path) throws FileNotFoundException
  {
    File file = new File(this.rootDirectory, path);
    if (!file.exists())
    {
      throw new FileNotFoundException();
    }
    file.delete();
  }

  @Override
  protected String urlForImplement(String path, Map<String, Object> options) throws FileNotFoundException
  {
    return "";
  }

  @Override
  protected void walkImplement(String path, Consumer<StatEntity> consumer) throws FileNotFoundException
  {
    List<String> children = this.getListImplement(path);
    children.forEach(child -> {
      try
      {
        consumer.accept(this.getStatImplement(child));
      }
      catch (FileNotFoundException ex) { }
    });
  }
}
