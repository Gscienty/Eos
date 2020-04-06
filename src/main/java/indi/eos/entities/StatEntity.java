package indi.eos.entities;

public class StatEntity
{
  private String path;
  private Long size;
  private Long time;
  private Boolean isDirectory;

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getPath()
  {
    return this.path;
  }

  public void setSize(Long size)
  {
    this.size = size;
  }

  public Long getSize()
  {
    return this.size;
  }

  public void setTime(Long time)
  {
    this.time = time;
  }

  public Long getTime()
  {
    return this.time;
  }

  public void setIsDirectory(Boolean isDirectory)
  {
    this.isDirectory = isDirectory;
  }
  
  public Boolean getIsDirectory()
  {
    return this.isDirectory;
  }
}
