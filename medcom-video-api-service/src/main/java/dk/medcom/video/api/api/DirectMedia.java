package dk.medcom.video.api.api;

public enum DirectMedia {
    never(0),
    best_effort(1);

    private final int value;

    DirectMedia(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
