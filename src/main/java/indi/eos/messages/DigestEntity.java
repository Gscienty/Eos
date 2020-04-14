package indi.eos.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.net.URLDecoder;
import java.util.regex.Pattern;

import indi.eos.exceptions.EosInvalidDigestException;
import indi.eos.exceptions.EosUnsupportedException;

public class DigestEntity
{
  private static final Pattern DIGEST_PATTERN = Pattern.compile("^.+:.+$");

  private String algorithm;
  private String hex;

  public static boolean isDigest(String reference) {
    return DIGEST_PATTERN.matcher(reference).matches();
  }

  public static DigestEntity toDigestEntity(String digest) throws EosInvalidDigestException
  {
    try {
      digest = URLDecoder.decode(digest, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new EosInvalidDigestException();
    }
    if (digest.indexOf(":") == -1)
    {
      throw new EosInvalidDigestException();
    }
    String[] digestSplited = digest.split(":");
    if (digestSplited.length != 2)
    {
      throw new EosInvalidDigestException();
    }

    DigestEntity entity = new DigestEntity();
    entity.setAlgorithm(digestSplited[0]);
    entity.setHex(digestSplited[1]);

    return entity;
  }

  public static DigestEntity toDigestEntity(InputStream input) throws EosUnsupportedException, IOException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] buffer = new byte[4096];
      int len = -1;
      while ((len = input.read(buffer)) != -1) {
        md.update(buffer, 0, len);
      }
      DigestEntity digest = new DigestEntity();
      digest.setAlgorithm("sha256");
      digest.setHex(md.digest());
      input.close();

      return digest;
    } catch (NoSuchAlgorithmException ex) {
      throw new EosUnsupportedException();
    }
  }

  public void setAlgorithm(String algorithm)
  {
    this.algorithm = algorithm;
  }

  public String getAlgorithm()
  {
    return this.algorithm;
  }

  public void setHex(String hex)
  {
    this.hex = hex;
  }

  public void setHex(byte[] hex) {
    StringBuilder builder = new StringBuilder();
    for (byte b : hex) {
      String tmp = Integer.toHexString(b & 0xff);
      if (tmp.length() == 1) {
        builder.append("0");
      }
      builder.append(tmp);
    }
    this.hex = builder.toString();
  }

  public String getHex()
  {
    return this.hex;
  }

  public boolean equals(DigestEntity digest) {
    return digest.getAlgorithm().equals(this.getAlgorithm()) && digest.getHex().equals(this.getHex());
  }

  public String getParameterValue() {
    return String.format("%s:%s", this.algorithm, this.hex);
  }
}
