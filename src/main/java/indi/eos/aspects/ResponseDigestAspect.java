package indi.eos.aspects;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import org.springframework.util.AntPathMatcher;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.web.servlet.HandlerMapping;

import indi.eos.exceptions.EosUnauthorizedException;

@Aspect
@Component
public class ResponseDigestAspect
{
  @Pointcut("@annotation(indi.eos.annotations.EosResponseDigest)")
  public void aspect() { }

  @AfterReturning("indi.eos.aspects.ResponseDigestAspect.aspect()")
  public void afterReturning() throws EosUnauthorizedException 
  {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

    String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
    String pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
    Map<String, String> pathVariables = new AntPathMatcher().extractUriTemplateVariables(pattern, path);

    String pathDigest = pathVariables.get("digest");
    if (pathDigest != null) {
      response.setHeader("Docker-Content-Digest", pathDigest);
    }
  }
}
