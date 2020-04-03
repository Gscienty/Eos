package indi.eos.exceptions.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import indi.eos.controllers.BaseController;
import indi.eos.exceptions.AuthenticationException;
import indi.eos.messages.ErrorEntity;

@ControllerAdvice
public class AuthenticationExceptionHandler
{
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ErrorEntity handleException(AuthenticationException ex)
  {
    ErrorEntity error = new ErrorEntity();
    error.setCode(HttpStatus.UNAUTHORIZED.value());
    error.setMessage("authentication required");
    error.setDetail("The access controller was unable to authenticate the client."
        + "Often this will be accompanied by a Www-Authenticate HTTP response header indicating how to authenticate.");

    return error;
  }
}
