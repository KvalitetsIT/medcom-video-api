package dk.medcom.vdx.organisation.api;

public class OrganisationDto {
    private String code;
    private String name;
    private int poolSize;
    private String smsSenderName;
    private String smsCallbackUrl;

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

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getSmsSenderName() {
        return smsSenderName;
    }

    public void setSmsSenderName(String smsSenderName) {
        this.smsSenderName = smsSenderName;
    }

    public String toString() {
        return "OrganisationDto{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", poolSize=" + poolSize +
                ", smsSenderName='" + smsSenderName + '\'' +
                '}';
    }

    public String getSmsCallbackUrl() {
        return smsCallbackUrl;
    }

    public void setSmsCallbackUrl(String smsCallbackUrl) {
        this.smsCallbackUrl = smsCallbackUrl;
    }
}
