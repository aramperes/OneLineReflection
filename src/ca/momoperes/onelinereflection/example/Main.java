package ca.momoperes.onelinereflection.example;

import ca.momoperes.onelinereflection.ReflectionProcessor;

public class Main {

    public static void main(String[] args) {
        objectFieldSameple();
        objectMethodSample();
        staticMethodSample();
    }

    private static void objectMethodSample() {
        Sheep context = new Sheep(true, new DyeColor((byte) 12));
        System.out.println(new ReflectionProcessor("$.getColor().getData()", context).process());
    }

    private static void objectFieldSameple() {
        Sheep context = new Sheep(true, new DyeColor((byte) 12));
        System.out.println(new ReflectionProcessor("$.sheared", context).process());
    }

    private static void staticMethodSample() {
        System.out.println(new ReflectionProcessor("ca.momoperes.onelinereflection.example.Main.staticTest()", null).process());

    }

    public static String staticTest() {
        return "Something static";
    }

}
