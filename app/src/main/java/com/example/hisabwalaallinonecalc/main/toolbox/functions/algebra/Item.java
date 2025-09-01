package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra;

/**
 * @author 30415
 */
public class Item {
    private String content;

    public Item(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
