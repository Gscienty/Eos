package indi.eos.messages;

public class CreateRepoEntity {
    private String name;
    private String driver;
    private FsCreateRepoEntity fs;

    public String getName() {
        return this.name;
    }

    public String getDriver() {
        return this.driver;
    }

    public FsCreateRepoEntity getFS() {
        return this.fs;
    }
}
