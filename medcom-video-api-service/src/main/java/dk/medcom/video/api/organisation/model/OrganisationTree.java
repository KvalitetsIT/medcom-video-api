package dk.medcom.video.api.organisation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public Set<String> extractAllOrganisationCodes() {
        if (children == null) return Set.of(code);
        var codes = children.stream()
                .flatMap(child -> child.extractAllOrganisationCodes().stream());
        return Stream.concat(Stream.of(code), codes).collect(Collectors.toSet());
    }
}
