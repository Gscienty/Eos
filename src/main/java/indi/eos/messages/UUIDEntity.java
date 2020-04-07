package indi.eos.messages;

import java.util.UUID;

public class UUIDEntity
{
  private String uuid;

  public static UUIDEntity generateUUID()
  {
    UUIDEntity result = new UUIDEntity();
    result.setUUID(UUID.randomUUID().toString());
    return result;
  }

  public void setUUID(String uuid)
  {
    this.uuid = uuid;
  }

  public String getUUID()
  {
    return this.uuid;
  }
}
