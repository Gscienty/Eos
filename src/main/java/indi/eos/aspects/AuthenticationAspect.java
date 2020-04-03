package indi.eos.aspects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import indi.eos.exceptions.AuthenticationException;

@Aspect
@Component
public class AuthenticationAspect
{

  @Pointcut("@annotation(indi.eos.annotations.EosAuthenticate)")
  public void aspect() { }

  @Before("indi.eos.aspects.AuthenticationAspect.aspect()")
  public void before() throws AuthenticationException
  {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String authenticateHeaderParameter = request.getHeader("WWW-Authenticate");
    if (authenticateHeaderParameter == null || !authenticateHeaderParameter.startsWith("Bearer "))
    {
      throw new AuthenticationException();
    }

    // TODO authenticate 
  }
}

