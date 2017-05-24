package ca.momoperes.onelinereflection.example;

public enum AnEnum {
    SOMETHING("hello");

    private final String string;

    AnEnum(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
