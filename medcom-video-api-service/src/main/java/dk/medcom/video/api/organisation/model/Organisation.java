package dk.medcom.video.api.organisation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation {
    private String code;
    private String name;
    private Integer poolSize;
    private Long groupId;
    private String smsSenderName;
    private boolean allowCustomUriWithoutDomain;
    private String smsCallbackUrl;
    private boolean policyServerEnabled;

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

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getSmsSenderName() {
        return smsSenderName;
    }

    public void setSmsSenderName(String smsSenderName) {
        this.smsSenderName = smsSenderName;
    }

    public boolean getAllowCustomUriWithoutDomain() {
        return allowCustomUriWithoutDomain;
    }

    public void setAllowCustomUriWithoutDomain(boolean allowCustomUriWithoutDomain) {
        this.allowCustomUriWithoutDomain = allowCustomUriWithoutDomain;
    }

    public String getSmsCallbackUrl() {
        return smsCallbackUrl;
    }

    public void setSmsCallbackUrl(String smsCallbackUrl) {
        this.smsCallbackUrl = smsCallbackUrl;
    }

    public boolean getPolicyServerEnabled() {
        return policyServerEnabled;
    }

    public void setPolicyServerEnabled(boolean policyServerEnabled) {
        this.policyServerEnabled = policyServerEnabled;
    }
}
