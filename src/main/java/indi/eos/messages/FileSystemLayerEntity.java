package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileSystemLayerEntity
{
  private String blobSum;

  @JsonProperty("blobSum")
  public void setBlobSum(String blobSum)
  {
    this.blobSum = blobSum;
  }

  @JsonProperty("blobSum")
  public String getBlobSum()
  {
    return this.blobSum;
  }
}
