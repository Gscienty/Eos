package indi.eos.exceptions;

public class InvalidPathException extends Exception
{
  private static final long serialVersionUID = 1; 

  private String driverName;
  private String path;

  public InvalidPathException(String driverName, String path)
  {
    this.driverName = driverName;
    this.path = path;
  }

  public String getDriverName()
  {
    return this.driverName;
  }

  public String getPath()
  {
    return this.path;
  }
}
