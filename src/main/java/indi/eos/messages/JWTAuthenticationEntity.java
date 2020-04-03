package indi.eos.messages;

import indi.eos.messages.JWTPayloadEntity;

public class JWTAuthenticationEntity extends JWTPayloadEntity
{
  private String name;

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }
}
