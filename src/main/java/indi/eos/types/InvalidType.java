package indi.eos.types;

public enum InvalidType
{
  NAME("name"), TAG("tag"), MANIFEST("manifest"), DIGEST("digest"), SIZE("size");

  private final String name;

  InvalidType(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }
}
