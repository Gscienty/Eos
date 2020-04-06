package indi.eos.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import indi.eos.entities.StatEntity;
import indi.eos.exceptions.InvalidOffsetException;
import indi.eos.exceptions.InvalidPathException;
import indi.eos.store.StorageDriver;

public abstract class BaseStorageDriver implements StorageDriver
{
  private static final Pattern PATH_PATTERN = Pattern.compile("^(/[A-Za-z0-9._-]+)+$");

  public byte[] getContent(String path) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }
    return this.getContentImplement(path);
  }

  public void putContent(String path, byte[] content) throws InvalidPathException, FileNotFoundException, IOException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }
    this.putContentImplement(path, content);
  }

  public InputStream reader(String path, long offset) throws InvalidPathException, InvalidOffsetException, FileNotFoundException
  {
    if (offset < 0)
    {
      throw new InvalidOffsetException(this.getName(), path, offset);
    }

    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    return this.readerImplement(path, offset);
  }

  public OutputStream writer(String path, boolean append) throws InvalidPathException, IOException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    return this.writerImplement(path, append);
  }

  public StatEntity getStat(String path) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path) && !path.equals("/"))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    return this.getStatImplement(path);
  }

  public List<String> getList(String path) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path) && !path.equals("/"))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    return this.getListImplement(path);
  }

  public void move(String sourcePath, String destPath) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(sourcePath))
    {
      throw new InvalidPathException(this.getName(), sourcePath);
    }
    if (!this.validatePath(destPath))
    {
      throw new InvalidPathException(this.getName(), destPath);
    }

    this.moveImplement(sourcePath, destPath);
  }

  public void delete(String path) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    this.deleteImplement(path);
  }

  public String urlFor(String path, Map<String, Object> options) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    return this.urlForImplement(path, options);
  }

  public void walk(String path, Consumer<StatEntity> consumer) throws InvalidPathException, FileNotFoundException
  {
    if (!this.validatePath(path))
    {
      throw new InvalidPathException(this.getName(), path);
    }

    this.walkImplement(path, consumer);
  }

  private boolean validatePath(String path)
  {
    return PATH_PATTERN.matcher(path).matches();
  }

  protected abstract byte[] getContentImplement(String path) throws FileNotFoundException;

  protected abstract void putContentImplement(String path, byte[] content) throws FileNotFoundException, IOException;

  protected abstract InputStream readerImplement(String path, long offset) throws InvalidOffsetException, FileNotFoundException;

  protected abstract OutputStream writerImplement(String path, boolean append) throws IOException;

  protected abstract StatEntity getStatImplement(String path) throws FileNotFoundException;

  protected abstract List<String> getListImplement(String path) throws FileNotFoundException;

  protected abstract void moveImplement(String sourcePath, String destPath) throws FileNotFoundException;

  protected abstract void deleteImplement(String path) throws FileNotFoundException;

  protected abstract String urlForImplement(String path, Map<String, Object> options) throws FileNotFoundException;

  protected abstract void walkImplement(String path, Consumer<StatEntity> consumer) throws FileNotFoundException;
}
