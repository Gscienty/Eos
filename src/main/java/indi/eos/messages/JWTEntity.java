package indi.eos.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import indi.eos.messages.JWTHeaderEntity;
import indi.eos.messages.JWTPayloadEntity;

public class JWTEntity<T>
{
  private JWTHeaderEntity header;
  private T payload;

  public void setHeader(JWTHeaderEntity header)
  {
    this.header = header;
  }

  public JWTHeaderEntity getHeader()
  {
    return this.header;
  }

  public void setPayload(T payload)
  {
    this.payload = payload;
  }

  public T getPayload()
  {
    return this.payload;
  }
}
