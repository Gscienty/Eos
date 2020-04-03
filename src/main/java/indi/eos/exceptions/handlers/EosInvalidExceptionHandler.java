package indi.eos.exceptions.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import indi.eos.controllers.BaseController;
import indi.eos.exceptions.EosInvalidException;
import indi.eos.messages.ErrorEntity;
import indi.eos.types.InvalidType;

@ControllerAdvice
public class EosInvalidExceptionHandler 
{
  @ExceptionHandler(EosInvalidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorEntity handleException(EosInvalidException ex)
  {
    ErrorEntity error = new ErrorEntity();
    error.setCode(HttpStatus.BAD_REQUEST.value());

    switch (ex.getInvalidType())
    {
      case NAME:
        error.setMessage("invalid repository name");
        break;

      case TAG:
        error.setMessage("manifest tag did not match URI");
        break;

      case MANIFEST:
        error.setMessage("repository name not known to registry");
        break;

      case DIGEST:
        error.setMessage("provided digest did not match uploaded content");
        break;

      case SIZE:
        error.setMessage("provided length did not match content length");
        break;
    }

    return error;
  }
}
