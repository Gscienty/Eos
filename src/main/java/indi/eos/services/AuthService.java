package indi.eos.services;

import indi.eos.exceptions.EosUnauthorizedException;

public interface AuthService
{
  void authorize() throws EosUnauthorizedException;
}
