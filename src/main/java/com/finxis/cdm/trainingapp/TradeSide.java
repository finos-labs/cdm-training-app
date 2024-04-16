package com.finxis.cdm.trainingapp;

import java.util.HashMap;
import java.util.Map;

public class TradeSide {
    static private final Map<String, TradeSide> known = new HashMap<>();
    static public final TradeSide BUY = new TradeSide("Buy");
    static public final TradeSide SELL = new TradeSide("Sell");

    static private final TradeSide[] array = {BUY, SELL};

    private final String name;

    private TradeSide(String name) {
        this.name = name;
        synchronized (TradeSide.class) {
            known.put(name, this);
        }
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    static public Object[] toArray() {
        return array;
    }

    public static TradeSide parse(String type) throws IllegalArgumentException {
        TradeSide result = known.get(type);
        if (result == null) {
            throw new IllegalArgumentException
            ("TradeSide: " + type + " is unknown.");
        }
        return result;
    }
}
