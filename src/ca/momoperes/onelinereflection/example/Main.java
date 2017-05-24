package ca.momoperes.onelinereflection.example;

import ca.momoperes.onelinereflection.ReflectionProcessor;

public class Main {

    public static void main(String[] args) {
        objectFieldSameple();
        objectMethodSample();
        staticMethodSample();
        parameterSample();
        multicontextualSample();
        enumSample();
    }

    private static void enumSample() {
        System.out.println(new ReflectionProcessor("ca.momoperes.onelinereflection.example.AnEnum.SOMETHING.getString()").process());
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
        System.out.println(new ReflectionProcessor("ca.momoperes.onelinereflection.example.Main.staticTest()").process());
    }

    private static void parameterSample() {
        Sheep context = new Sheep(true, new DyeColor((byte) 12));
        System.out.println(new ReflectionProcessor("$.say(\"a\", false, 9000)", context).process());
    }

    private static void multicontextualSample() {
        Sheep context1 = new Sheep(false, new DyeColor((byte) 1));
        Sheep context2 = new Sheep(true, new DyeColor((byte) 2));
        System.out.println(new ReflectionProcessor("ca.momoperes.onelinereflection.example.Main.meet($1, $2)", context1, context2).process());
    }

    public static String meet(Sheep sheep1, Sheep sheep2) {
        return "Sheep " + sheep1.getColor().getData() + " meets sheep " + sheep2.getColor().getData();
    }

    public static String staticTest() {
        return "Something static";
    }

}
