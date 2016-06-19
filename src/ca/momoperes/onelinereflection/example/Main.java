package ca.momoperes.onelinereflection.example;

import ca.momoperes.onelinereflection.ReflectionProcessor;

public class Main {

    public static void main(String[] args) {
        Sheep context = new Sheep(true, new DyeColor((byte) 12));
        System.out.println(new ReflectionProcessor("$.getColor().getData()", context).process());
    }

}
