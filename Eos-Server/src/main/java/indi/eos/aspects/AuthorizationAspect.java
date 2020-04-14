package indi.eos.aspects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import indi.eos.exceptions.EosUnauthorizedException;
import indi.eos.services.AuthService;

@Aspect
@Component
public class AuthorizationAspect
{
  @Autowired
  private AuthService authService;

  @Pointcut("@annotation(indi.eos.annotations.EosAuthorize)")
  public void aspect() { }

  @Before("indi.eos.aspects.AuthorizationAspect.aspect()")
  public void before() throws EosUnauthorizedException 
  {
    this.authService.authorize();
  }
}
