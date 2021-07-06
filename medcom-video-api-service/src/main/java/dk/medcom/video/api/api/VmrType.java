package dk.medcom.video.api.api;

public enum VmrType {
    CONFERENCE(0),
    LECTURE(1);

    private final int value;

    private VmrType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
