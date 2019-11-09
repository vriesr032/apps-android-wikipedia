package org.wikipedia.dataclient.mwapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import org.wikipedia.json.GsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class EditorTaskCounts {
    @Nullable private JsonElement counts;

    @NonNull
    public Map<String, Integer> getDescriptionEditsPerLanguage() {
        Map<String, Integer> editsPerLanguage = null;
        if (counts != null && !(counts instanceof JsonArray)) {
            editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appDescriptionEdits;
        }
        return editsPerLanguage == null ? Collections.emptyMap() : editsPerLanguage;
    }

    @NonNull
    public Map<String, Integer> getCaptionEditsPerLanguage() {
        Map<String, Integer> editsPerLanguage = null;
        if (counts != null && !(counts instanceof JsonArray)) {
            editsPerLanguage = GsonUtil.getDefaultGson().fromJson(counts, Counts.class).appCaptionEdits;
        }
        return editsPerLanguage == null ? Collections.emptyMap() : editsPerLanguage;
    }

    public int getTotalEdits() {
        int totalEdits = 0;
        for (int count : getDescriptionEditsPerLanguage().values()) {
            totalEdits += count;
        }
        for (int count : getCaptionEditsPerLanguage().values()) {
            totalEdits += count;
        }
        return totalEdits;
    }

    public int getDescriptionEditTargetsPassedCount() {
        int count = getTotalEdits();
        for (int target : getDescriptionEditTargets()) {
            if (count > target) {
                count++;
            }
        }
        return count;
    }

    public int getCaptionEditTargetsPassedCount() {
        int count = getTotalEdits();
        for (int target : getCaptionEditTargets()) {
            if (count > target) {
                count++;
            }
        }
        return count;
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @NonNull
    public List<Integer> getDescriptionEditTargets() {
        return Arrays.asList(3, 15);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @NonNull
    public List<Integer> getCaptionEditTargets() {
        return Arrays.asList(3, 15);
    }

    public class Counts {
        @Nullable @SerializedName("app_description_edits") private Map<String, Integer> appDescriptionEdits;
        @Nullable @SerializedName("app_caption_edits") private Map<String, Integer> appCaptionEdits;
    }
}
