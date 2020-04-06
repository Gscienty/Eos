package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorEntity
{
  private String code;
  private String message;
  private Object detail;

  @JsonProperty("code")
  public void setCode(String code)
  {
    this.code = code;
  }

  @JsonProperty("code")
  public String getCode()
  {
    return this.code;
  }

  @JsonProperty("message")
  public void setMessage(String message)
  {
    this.message = message;
  }

  @JsonProperty("message")
  public String getMessage()
  {
    return this.message;
  }

  @JsonProperty("detail")
  public void setDetail(Object detail)
  {
    this.detail = detail;
  }

  @JsonProperty("detail")
  public Object getDetail()
  {
    return this.detail;
  }
}
