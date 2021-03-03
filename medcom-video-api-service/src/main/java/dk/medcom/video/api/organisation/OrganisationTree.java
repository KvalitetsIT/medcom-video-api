package dk.medcom.video.api.organisation;

import java.util.ArrayList;
import java.util.List;

public class OrganisationTree {
    private int poolSize;
    private String code;
    private String name;
    private List<OrganisationTree> children = new ArrayList<>();

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganisationTree> getChildren() {
        return children;
    }

    public void setChildren(List<OrganisationTree> children) {
        this.children = children;
    }
}
