package dk.medcom.vdx.organisation.api;

import java.util.ArrayList;
import java.util.List;

public class OrganisationTreeDto {
    private int poolSize;
    private String code;
    private String name;
    private List<OrganisationTreeDto> children = new ArrayList<>();

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

    public List<OrganisationTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<OrganisationTreeDto> children) {
        this.children = children;
    }
}
