package dev.flxwdns.multistom;

public final class MultiStomBootstrap {

    public static void main(String[] args) {
        System.setProperty("multistom.startup", String.valueOf(System.currentTimeMillis()));

        new MultiStom();
    }
}
