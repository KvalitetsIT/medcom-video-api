package dk.medcom.video.api.api;

public enum ViewType {
    ONE_MAIN_ZERO_PIPS(0),
    ONE_MAIN_SEVEN_PIPS(1),
    ONE_MAIN_TWENTYONE_PIPS(2),
    TWO_MAINS_TWENTYONE_PIPS(3),
    FOUR_MAINS_ZERO_PIPS(4);

    private final int value;

    private ViewType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
