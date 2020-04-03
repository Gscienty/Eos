package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorEntity
{
  private Integer code;
  private String message;
  private Object detail;

  @JsonProperty("code")
  public void setCode(Integer code)
  {
    this.code = code;
  }

  @JsonProperty("code")
  public Integer getCode()
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
