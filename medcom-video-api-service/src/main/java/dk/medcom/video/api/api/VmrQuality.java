package dk.medcom.video.api.api;

public enum VmrQuality {
    SD(0),
    HD(1),
    FULL_HD(2);

    private final int value;

    private VmrQuality(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
