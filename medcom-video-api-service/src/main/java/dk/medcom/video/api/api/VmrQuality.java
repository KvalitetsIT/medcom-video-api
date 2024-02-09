package dk.medcom.video.api.api;

public enum VmrQuality {
    sd(0),
    hd(1),
    fullhd(2);

    private final int value;

    VmrQuality(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
