package indi.eos.messages;

import java.util.Date;

public class JWTPayloadEntity
{
  private String issue;
  private String subject;
  private String audience;
  private Date expired;
  private Date notBefore;
  private Date issueAt;
  private String id;

  public void setIssue(String issue)
  {
    this.issue = issue;
  }

  public String getIssue(String issue)
  {
    return this.issue;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public String getSubject()
  {
    return this.subject;
  }

  public void setAudience(String audience)
  {
    this.audience = audience;
  }

  public String getAudience()
  {
    return this.audience;
  }

  public void setExpired(Date expired)
  {
    this.expired = expired;
  }

  public Date getExpired()
  {
    return this.expired;
  }

  public void setNotBefore(Date notBefore)
  {
    this.notBefore = notBefore;
  }

  public Date getNotBefore()
  {
    return this.notBefore;
  }

  public void setIssueAt(Date issueAt)
  {
    this.issueAt = issueAt;
  }

  public Date getIssueAt()
  {
    return this.issueAt;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return this.id;
  }
}
