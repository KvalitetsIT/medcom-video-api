package dk.medcom.vdx.organisation.api;

public class OrganisationUriDto {
    public OrganisationUriDto(){
    }

    public OrganisationUriDto(String code, String name, long groupId, String groupName, String uri){
        this.code = code;
        this.name = name;
        this.groupId = groupId;
        this.groupName = groupName;
        this.uri = uri;
    }

    private String code;
    private String name;
    private long groupId;
    private String groupName;
    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
