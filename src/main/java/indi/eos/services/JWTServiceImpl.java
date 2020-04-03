package indi.eos.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import indi.eos.exceptions.JWTDeserializeException;
import indi.eos.exceptions.JWTValidSignatureFailedException;
import indi.eos.messages.JWTEntity;
import indi.eos.messages.JWTHeaderEntity;
import indi.eos.messages.JWTPayloadEntity;


@Service
public class JWTServiceImpl<T extends JWTPayloadEntity> implements JWTService<T>
{
  private final ObjectMapper mapper;
  private final Base64.Encoder encoder;
  private final Base64.Decoder decoder;

  public JWTServiceImpl()
  {
    this.mapper = new ObjectMapper();
    this.encoder = Base64.getEncoder();
    this.decoder = Base64.getDecoder();
  }

  public String serialize(JWTEntity<T> entity, byte[] secret) throws
    JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException
    {
      Mac mac = Mac.getInstance(entity.getHeader().getAlgorithm());
      mac.init(new SecretKeySpec(secret, entity.getHeader().getAlgorithm()));

      String header = this.encoder.encodeToString(mapper.writeValueAsString(entity.getHeader()).getBytes("UTF-8"));
      String payload = this.encoder.encodeToString(mapper.writeValueAsString(entity.getPayload()).getBytes("UTF-8"));
      String encodedString = header + "." + payload;
      byte[] signatureArr = mac.doFinal(encodedString.getBytes("UTF-8"));

      return encodedString + "." + this.encoder.encodeToString(signatureArr); 
    }

  public JWTEntity<T> deserialize(String jwt, byte[] secret, Class<T> payloadClass) throws
    JWTDeserializeException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
    JWTValidSignatureFailedException, IOException
  {
    String[] splitedJWT = jwt.split("\\.");
    if (splitedJWT.length != 3)
    {
      throw new JWTDeserializeException();
    }
    JWTEntity<T> entity = new JWTEntity<>();
    entity.setHeader(this.mapper.readValue(new String(this.decoder.decode(splitedJWT[0]), "UTF-8"), JWTHeaderEntity.class));
    entity.setPayload(this.mapper.readValue(new String(this.decoder.decode(splitedJWT[1]), "UTF-8"), payloadClass));

    String encodedString = splitedJWT[0] + "." + splitedJWT[1];
    Mac mac = Mac.getInstance(entity.getHeader().getAlgorithm());
    mac.init(new SecretKeySpec(secret, entity.getHeader().getAlgorithm()));

    byte[] calcSignature = mac.doFinal(encodedString.getBytes("UTF-8"));
    byte[] jwtSignature = this.decoder.decode(splitedJWT[2]);
    if (!Arrays.equals(calcSignature, jwtSignature)) {
      throw new JWTValidSignatureFailedException();
    }

    return entity;
  }

  public Boolean valid(String jwt, byte[] secret) throws
    JWTDeserializeException, JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
    JWTValidSignatureFailedException, IOException
  {
    String[] splitedJWT = jwt.split("\\.");
    if (splitedJWT.length != 3)
    {
      throw new JWTDeserializeException();
    }
    JWTHeaderEntity header = this.mapper.readValue(new String(this.decoder.decode(splitedJWT[0]), "UTF-8"), JWTHeaderEntity.class);

    String encodedString = splitedJWT[0] + "." + splitedJWT[1];
    Mac mac = Mac.getInstance(header.getAlgorithm());
    mac.init(new SecretKeySpec(secret, header.getAlgorithm()));

    byte[] calcSignature = mac.doFinal(encodedString.getBytes("UTF-8"));
    byte[] jwtSignature = this.decoder.decode(splitedJWT[2]);
    return Arrays.equals(calcSignature, jwtSignature);
  }
}
