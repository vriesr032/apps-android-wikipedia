package org.wikipedia.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.Nullable;

public final class ClipboardUtil {
    public static void setPlainText(@Nullable Context context,
                                    @Nullable CharSequence label,
                                    @Nullable CharSequence text) {
        ClipData clip = ClipData.newPlainText(label, text);
        try {
            getManager(context).setPrimaryClip(clip);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.toString());
        }
    }

    private static ClipboardManager getManager(Context context) {
        try {
            return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.toString());
        }
    }

    private ClipboardUtil() { }
}
