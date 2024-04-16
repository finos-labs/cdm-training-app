package com.finxis.cdm.trainingapp;

import java.util.HashMap;
import java.util.Map;

public class TradeSymbol {
    static private final Map<String, TradeSymbol> known = new HashMap<>();
    static public final TradeSymbol  Gilt0522jul2022 = new TradeSymbol("Gilt 0.5% 22jul2022");


    static private final TradeSymbol[] array = {Gilt0522jul2022};

    private final String name;

    private TradeSymbol(String name) {
        this.name = name;
        synchronized (TradeSymbol.class) {
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

    public static TradeSymbol parse(String type) throws IllegalArgumentException {
        TradeSymbol result = known.get(type);
        if (result == null) {
            throw new IllegalArgumentException
            ("TradeSymbol: " + type + " is unknown.");
        }
        return result;
    }
}
