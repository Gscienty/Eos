package indi.eos.exceptions.handlers;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosInvalidParameterException;
import indi.eos.exceptions.EosUnauthorizedException;
import indi.eos.exceptions.EosUnsupportedException;
import indi.eos.exceptions.StorageDriverNotFoundException;
import indi.eos.messages.ErrorEntity;
import indi.eos.messages.ErrorsEntity;

@ControllerAdvice
public class EosExceptionHandler
{
  @ExceptionHandler(EosUnsupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ResponseBody
  public ErrorsEntity handleUnsupportedException(EosUnsupportedException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("UNSUPPORTED");

    return this.singleErrorResult(entity);
  }

  @ExceptionHandler(EosUnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorsEntity handleUnauthorizedException(EosUnauthorizedException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("UNAUTHORIZED");

    return this.singleErrorResult(entity);
  }

  @ExceptionHandler(EosInvalidDigestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsEntity handleInvalidDigestException(EosInvalidDigestException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("DIGEST_INVALID");

    return this.singleErrorResult(entity);
  }

  @ExceptionHandler(FileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorsEntity handleFileNotFOundException(FileNotFoundException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("BLOB_UNKNOWN");
    entity.setMessage("blob unknown to registry.");

    return this.singleErrorResult(entity);
  }

  @ExceptionHandler(StorageDriverNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorsEntity handleFileNotFOundException(StorageDriverNotFoundException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("NAME_UNKNOWN");
    entity.setMessage("repository name not known to registry.");

    return this.singleErrorResult(entity);
  }

  @ExceptionHandler(EosInvalidParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorsEntity handleEosInvalidParameterException(EosInvalidParameterException ex)
  {
    ErrorEntity entity = new ErrorEntity();
    entity.setCode("NAME_UNKNOWN");
    entity.setMessage("repository name not known to registry.");

    return this.singleErrorResult(entity);
  }

  private ErrorsEntity singleErrorResult(ErrorEntity entity)
  {
    ErrorsEntity result = new ErrorsEntity();
    result.setErrors(new ArrayList<ErrorEntity>(1));
    result.getErrors().add(entity);
    return result;
  }
}
