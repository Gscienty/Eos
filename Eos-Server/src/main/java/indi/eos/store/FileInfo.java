package indi.eos.store;

public interface FileInfo {
  String name();

  String path();

  long size();

  long modifyTime();

  boolean isDirectory();
}
