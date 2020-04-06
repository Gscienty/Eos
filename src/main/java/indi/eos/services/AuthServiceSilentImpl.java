package indi.eos.services;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.EosUnauthorizedException;
import indi.eos.services.AuthService;

@Service
public class AuthServiceSilentImpl implements AuthService
{
  public void authorize() throws EosUnauthorizedException { }
}
