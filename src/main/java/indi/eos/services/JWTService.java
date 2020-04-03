package indi.eos.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import indi.eos.messages.JWTEntity;
import indi.eos.messages.JWTPayloadEntity;
import indi.eos.exceptions.JWTDeserializeException;
import indi.eos.exceptions.JWTValidSignatureFailedException;

public interface JWTService<T extends JWTPayloadEntity>
{
  public String serialize(JWTEntity<T> entity, byte[] secret) throws
    JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException;

  public JWTEntity<T> deserialize(String jwt, byte[] secret, Class<T> payloadClass) throws
    JWTDeserializeException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
    JWTValidSignatureFailedException, IOException;
}
