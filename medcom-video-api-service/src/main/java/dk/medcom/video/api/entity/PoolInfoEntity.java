package dk.medcom.video.api.entity;

public class PoolInfoEntity {

    private String organisationCode;

    private String name;

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
}
