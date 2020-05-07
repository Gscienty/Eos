package indi.eos.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;


@Entity
@Table(name = "repository")
public class RepositoryEntity {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;
  @Column(name = "name", nullable = false, length = 64)
  private String name;
  @Column(name = "driver", nullable = false, length = 32)
  private String driver;

  public Long getID() {
      return this.id;
  }

  public String getName() {
      return this.name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public String getDriver() {
      return this.driver;
  }

  public void setDriver(String driver) {
      this.driver = driver;
  }
}

