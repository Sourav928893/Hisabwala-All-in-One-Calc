package com.example.hisabwalaallinonecalc.main.toolbox;



import android.graphics.drawable.Drawable;

import java.util.Objects;

/**
 * @author 30415
 */
public final class ToolBoxItem {
    private final int id;
    private final String title;
    private final Drawable drawable;

    /**
     *
     */
    public ToolBoxItem(int id, String title, Drawable drawable) {
        this.id = id;
        this.title = title;
        this.drawable = drawable;
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Drawable drawable() {
        return drawable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ToolBoxItem) obj;
        return this.id == that.id &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.drawable, that.drawable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, drawable);
    }

    @Override
    public String toString() {
        return "ToolBoxItem[" +
                "id=" + id + ", " +
                "title=" + title + ", " +
                "drawable=" + drawable + ']';
    }

}