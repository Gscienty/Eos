package indi.eos.exceptions;

import indi.eos.types.InvalidType;

public class EosInvalidException extends Exception
{
  private static final long serialVersionUID = 1000006;

  private InvalidType type;

  public EosInvalidException(InvalidType type)
  {
    this.type = type;
  }

  public InvalidType getInvalidType()
  {
    return this.type;
  }
}
