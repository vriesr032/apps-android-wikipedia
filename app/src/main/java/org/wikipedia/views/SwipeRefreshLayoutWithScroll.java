package org.wikipedia.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SwipeRefreshLayoutWithScroll extends SwipeRefreshLayout {

    public SwipeRefreshLayoutWithScroll(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutWithScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View scrollableView;
    public void setScrollableChild(View scrollableView) {
        this.scrollableView = scrollableView;
    }

    @Override
    public boolean canChildScrollUp() {
        if (scrollableView == null) {
            return false;
        }
        return scrollableView.getScrollY() > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (RuntimeException e) {
            return false;
        }
    }
}
