package indi.eos.messages;

import java.util.List;

import indi.eos.messages.ErrorEntity;

public class ErrorsEntity
{
  private List<ErrorEntity> errors;

  public void setErrors(List<ErrorEntity> errors)
  {
    this.errors = errors;
  }

  public List<ErrorEntity> getErrors()
  {
    return this.errors;
  }
}
