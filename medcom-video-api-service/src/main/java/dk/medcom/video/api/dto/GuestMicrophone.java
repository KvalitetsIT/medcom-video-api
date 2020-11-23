package dk.medcom.video.api.dto;

public enum GuestMicrophone {
    OFF(0),
    ON(1),
    MUTED(2);

    private final int value;

    private GuestMicrophone(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
