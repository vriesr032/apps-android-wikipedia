package androidx.drawerlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FixedDrawerLayout extends DrawerLayout {
    public FixedDrawerLayout(Context context) {
        super(context);
    }

    public FixedDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSlidingEnabled(boolean enable) {
        if (enable) {
            this.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            this.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    boolean isContentView(View child) {
        if (child == null) {
            return false;
        }
        return super.isContentView(child);
    }
}
