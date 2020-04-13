package indi.eos.messages;

import indi.eos.exceptions.EosInvalidDigestException;

public class DigestEntity
{
  private String algorithm;
  private String hex;

  public static DigestEntity toDigestEntity(String digest) throws EosInvalidDigestException
  {
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
}
