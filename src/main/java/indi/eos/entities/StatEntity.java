package indi.eos.entities;

public class StatEntity {
  private String path;
  private long size;
  private long time;
  private boolean isDirectory;
  private boolean exists;
  private long order;

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return this.path;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getSize() { return this.size;
  }

  public void setTime(long time) { this.time = time;
  }

  public long getTime() { return this.time;
  }

  public void setIsDirectory(boolean isDirectory) {
    this.isDirectory = isDirectory;
  }
  
  public boolean getIsDirectory() {
    return this.isDirectory;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
  }

  public boolean getExists() {
    return this.exists;
  }

  public void setOrder(long order) {
    this.order = order;
  }

  public long getOrder() {
    return this.order;
  }
}
