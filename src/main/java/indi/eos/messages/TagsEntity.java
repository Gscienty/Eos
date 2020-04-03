package indi.eos.messages;

import java.util.List;

public class TagsEntity
{
  private String name;
  private List<String> tags;

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public void setTags(List<String> tags)
  {
    this.tags = tags;
  }

  public List<String> getTags()
  {
    return this.tags;
  }
}
