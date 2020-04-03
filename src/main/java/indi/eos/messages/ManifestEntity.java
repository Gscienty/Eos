package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ManifestEntity
{
  private String name;
  private String tag;
  private List<FileSystemLayerEntity> fsLayers;
  private Object history;
  private String signature;

  @JsonProperty("name")
  public void setName(String name)
  {
    this.name = name;
  }

  @JsonProperty("name")
  public String getName()
  {
    return this.name;
  }

  @JsonProperty("tag")
  public void setTag(String tag)
  {
    this.tag = tag;
  }

  @JsonProperty("tag")
  public String getTag()
  {
    return this.tag;
  }

  @JsonProperty("fsLayers")
  public void setFSLayers(List<FileSystemLayerEntity> fsLayers)
  {
    this.fsLayers = fsLayers;
  }

  @JsonProperty("fsLayers")
  public List<FileSystemLayerEntity> getFSLayer()
  {
    return this.fsLayers;
  }

  @JsonProperty("history")
  public void setHistory(Object history)
  {
    this.history = history;
  }

  @JsonProperty("history")
  public Object getHistory()
  {
    return this.history;
  }

  @JsonProperty("signature")
  public void setSignature(String signature)
  {
    this.signature = signature;
  }

  @JsonProperty("signature")
  public String getSignature()
  {
    return this.signature;
  }
}
