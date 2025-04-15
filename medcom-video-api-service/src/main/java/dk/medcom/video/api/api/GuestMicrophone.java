package dk.medcom.video.api.api;

public enum GuestMicrophone {
    off(0),
    on(1),
    muted(2);

    private final int value;

    GuestMicrophone(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
