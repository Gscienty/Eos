package indi.eos.entities.reference;

import indi.eos.entities.reference.Reference;

public class Field implements Reference
{
  private String field;

  public void setField(String field)
  {
    this.field = field;
  }

  public String getField()
  {
    return this.field;
  }

  public byte[] mashal()
  {
    return this.field.getBytes();
  }

  public void unmarshal(byte[] buf)
  {
    this.field = new String(buf);
  }

  @Override
  public String ref()
  {
    return this.field;
  }
}
