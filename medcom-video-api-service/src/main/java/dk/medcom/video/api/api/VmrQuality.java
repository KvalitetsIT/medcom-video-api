package dk.medcom.video.api.api;

public enum VmrQuality {
    sd(0),
    hd(1),
    full_hd(2);

    private final int value;

    private VmrQuality(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
