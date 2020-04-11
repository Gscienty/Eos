package indi.eos.store;

public interface FileInfo {
  String path();

  long size();

  long modifyTime();

  boolean isDirectory();
}
