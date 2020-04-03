package indi.eos.exceptions.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import indi.eos.controllers.BaseController;
import indi.eos.exceptions.RepositoryNotFoundException;
import indi.eos.messages.ErrorEntity;

@ControllerAdvice
public class RepositoryNotFoundExceptionHandler
{
  @ExceptionHandler(RepositoryNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorEntity handleException(RepositoryNotFoundException ex)
  {
    ErrorEntity error = new ErrorEntity();
    error.setCode(HttpStatus.NOT_FOUND.value());
    error.setMessage("repository name not known to registry");
    error.setDetail("This is returned if the name used during an operation is unknown to the registry");

    return error;
  }
}
