package pers.warren.ioc.config;

public abstract class AbstractConfigReader implements IConfigReader{

    protected String path;

    public AbstractConfigReader(String path) {
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }

    public abstract void read();
}
