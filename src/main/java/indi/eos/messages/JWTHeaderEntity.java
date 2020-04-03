package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTHeaderEntity
{
  private String type;
  private String algorithm;

  @JsonProperty("typ")
  public void setType(String type)
  {
    this.type = type;
  }

  @JsonProperty("typ")
  public String getType()
  {
    return this.type;
  }

  @JsonProperty("alg")
  public void setAlgorithm(String algorithm)
  {
    this.algorithm = algorithm;
  }

  @JsonProperty("alg")
  public String getAlgorithm()
  {
    return this.algorithm;
  }
}

