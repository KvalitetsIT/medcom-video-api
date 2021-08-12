package dk.medcom.video.api.entity;

public class PoolInfoEntity {

    private String organisationCode;

    private String organisationName;

    private int availablePoolSize;

    private int wantedPoolSize;
    
	public String getOrganisationCode() {
		return organisationCode;
	}

	public void setOrganisationCode(String organisationCode) {
		this.organisationCode = organisationCode;
	}

	public int getAvailablePoolSize() {
		return availablePoolSize;
	}

	public void setAvailablePoolSize(int availablePoolSize) {
		this.availablePoolSize = availablePoolSize;
	}

	public int getWantedPoolSize() {
		return wantedPoolSize;
	}

	public void setWantedPoolSize(int wantedPoolSize) {
		this.wantedPoolSize = wantedPoolSize;
	}

	public String getOrganisationName() {
		return organisationName;
	}

	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}
}
