package dk.medcom.video.api.api;

public enum ViewType {
    one_main_zero_pips(0),
    one_main_seven_pips(1),
    one_main_twentyone_pips(2),
    two_mains_twentyone_pips(3),
    four_mains_zero_pips(4);

    private final int value;

    private ViewType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

}
