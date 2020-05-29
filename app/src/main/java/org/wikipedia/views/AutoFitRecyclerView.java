package org.wikipedia.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.wikipedia.R;
import org.wikipedia.util.log.L;

/** {@link RecyclerView} that invokes a callback when the number of columns should be updated. */
public class AutoFitRecyclerView extends RecyclerView {
    public interface Callback {
        void onColumns(int columns);
    }

    private static final int MIN_COLUMNS = 1;

    @IntRange(from = MIN_COLUMNS) private int columns = MIN_COLUMNS;
    private int minColumnWidth;

    @NonNull private Callback callback = new DefaultCallback();

    public AutoFitRecyclerView(Context context) {
        super(context, null);
        init(null, 0);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @IntRange(from = MIN_COLUMNS) public int getColumns() {
        return columns;
    }

    public void minColumnWidth(int minColumnWidth) {
        if (this.minColumnWidth != minColumnWidth) {
            this.minColumnWidth = minColumnWidth;
            requestLayout();
        }
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback == null ? new DefaultCallback() : callback;
    }

    @Override protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int cols = calculateColumns(minColumnWidth, getMeasuredWidth());
        if (this.columns != cols) {
            this.columns = cols;
            callback.onColumns(this.columns);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            super.onLayout(changed, l, t, r, b);
        } catch (Exception e) {
            L.logRemoteErrorIfProd(e);
        }
    }

    private int calculateColumns(int columnWidth, int availableWidth) {
        return columnWidth > 0 ? Math.max(MIN_COLUMNS, availableWidth / columnWidth) : MIN_COLUMNS;
    }

    private void init(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs,
                    R.styleable.AutoFitRecyclerView, defStyleAttr, 0);
            minColumnWidth = array.getDimensionPixelSize(R.styleable.AutoFitRecyclerView_minColumnWidth, 0);
            array.recycle();
        }
    }

    public static class DefaultCallback implements Callback {
        @Override public void onColumns(int columns) { }
    }
}
