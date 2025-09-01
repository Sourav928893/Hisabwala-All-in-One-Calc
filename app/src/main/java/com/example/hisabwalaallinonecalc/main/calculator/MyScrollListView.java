package com.example.hisabwalaallinonecalc.main.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Custom ListView to fix scroll conflicts when nested inside another scrollable parent.
 */
public class MyScrollListView extends ListView {
    private float preY = 0, preX = 0;

    public MyScrollListView(Context context) {
        super(context);
    }

    public MyScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                preY = ev.getY();
                preX = ev.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float deltaY = ev.getY() - preY;
                float deltaX = ev.getX() - preX;

                // If horizontal movement is larger, let parent handle it
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // Vertical scrolling
                    if (isAtTop() && deltaY > 0) {
                        // Trying to scroll up when already at top → let parent handle
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else if (isAtBottom() && deltaY < 0) {
                        // Trying to scroll down when already at bottom → let parent handle
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        // Otherwise, consume inside ListView
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                // Reset: allow parent to intercept again
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /** Check if ListView is scrolled to the top */
    private boolean isAtTop() {
        return getFirstVisiblePosition() == 0 &&
                (getChildCount() == 0 || getChildAt(0).getTop() >= 0);
    }

    /** Check if ListView is scrolled to the bottom */
    private boolean isAtBottom() {
        return getLastVisiblePosition() == getCount() - 1 &&
                (getChildCount() == 0 || getChildAt(getChildCount() - 1).getBottom() <= getHeight());
    }
}
