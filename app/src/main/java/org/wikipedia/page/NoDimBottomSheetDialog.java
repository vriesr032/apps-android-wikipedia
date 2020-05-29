package org.wikipedia.page;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * Descendant of BottomSheetDialog that prevents the background from being dimmed.
 */
public class NoDimBottomSheetDialog extends BottomSheetDialog {
    public NoDimBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setDimAmount(0f);
    }

    protected void startExpanded() {
        getWindow().getDecorView().post(() -> {
            FrameLayout bottomSheet = getWindow().getDecorView().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }
}
