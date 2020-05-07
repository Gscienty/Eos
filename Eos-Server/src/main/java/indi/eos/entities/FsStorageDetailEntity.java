package indi.eos.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fs_storage_detail")
public class FsStorageDetailEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Column(name = "dir")
    private String rootPath;
    @Column(name = "repo_ref_id")
    private Long refID;

    public Long getID() {
        return this.id;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public Long getRefID() {
        return this.refID;
    }

    public void setRefID(Long refID) {
        this.refID = refID;
    }
}
