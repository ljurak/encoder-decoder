package app;

public enum ProgramMode {

    ENCODE("encode"),
    DECODE("decode"),
    SEND("send");

    private String mode;

    private ProgramMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static ProgramMode valueOfMode(String value) {
        for (ProgramMode mode : values()) {
            if (mode.getMode().equals(value)) {
                return  mode;
            }
        }
        return null;
    }
}
