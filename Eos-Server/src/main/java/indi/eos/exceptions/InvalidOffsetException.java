package indi.eos.exceptions;

public class InvalidOffsetException extends Exception
{
  private static final long serialVersionUID = 2; 

  private String driverName;
  private String path;
  private long offset;

  public InvalidOffsetException(String driverName, String path, long offset)
  {
    this.driverName = driverName;
    this.path = path;
    this.offset = offset;
  }

  public String getDriverName()
  {
    return this.driverName;
  }

  public String getPath()
  {
    return this.path;
  }

  public long getOffset()
  {
    return this.offset;
  }
}
