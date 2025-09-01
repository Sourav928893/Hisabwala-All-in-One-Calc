package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency;

public class Currency {
    private final int flagResId;
    private final String symbol;
    private final String chineseName;
    private final String englishName;

    public Currency(int flagResId, String symbol, String chineseName, String englishName) {
        this.flagResId = flagResId;
        this.symbol = symbol;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public int id() {
        return flagResId;
    }

    public String symbol() {
        return symbol;
    }

    public String chineseName() {
        return chineseName;
    }

    public String englishName() {
        return englishName;
    }
}
