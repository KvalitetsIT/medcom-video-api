package dk.medcom.video.api.api;

public enum VmrType {
    conference(0),
    lecture(1);

    private final int value;

    private VmrType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
