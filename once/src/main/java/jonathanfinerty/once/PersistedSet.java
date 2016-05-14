package jonathanfinerty.once;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class PersistedSet {

    private static final String STRING_SET_KEY = "PersistedSetValues";
    private static final String DELIMITER = ",";

    private SharedPreferences preferences;
    private Set<String> set = new HashSet<>();

    private final AsyncSharedPreferenceLoader preferenceLoader;

    public PersistedSet(Context context, String setName) {
        String preferencesName = "PersistedSet".concat(setName);
        preferenceLoader = new AsyncSharedPreferenceLoader(context, preferencesName);
    }

    private void waitForLoad() {
        if (preferences == null) {
            preferences = preferenceLoader.get();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                set = preferences.getStringSet(STRING_SET_KEY, new HashSet<String>());
            } else {
                String setString = preferences.getString(STRING_SET_KEY, null);
                set = new HashSet<>(StringToStringSet(setString));
            }
        }
    }

    public void put(String tag) {
        waitForLoad();

        set.add(tag);
        updatePreferences();
    }

    public boolean contains(String tag) {
        waitForLoad();

        return set.contains(tag);
    }

    public void remove(String tag) {
        waitForLoad();

        set.remove(tag);
        updatePreferences();
    }

    public void clear() {
        waitForLoad();

        set.clear();
        updatePreferences();
    }

    private void updatePreferences() {
        SharedPreferences.Editor edit = preferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            edit.putStringSet(STRING_SET_KEY, set);
        } else {
            edit.putString(STRING_SET_KEY, StringSetToString(set));
        }
        edit.apply();
    }

    private String StringSetToString(Set<String> set) {
        StringBuilder stringBuilder = new StringBuilder();
        String loopDelimiter = "";

        for (String s : set) {
            stringBuilder.append(loopDelimiter);
            stringBuilder.append(s);

            loopDelimiter = DELIMITER;
        }

        return stringBuilder.toString();
    }

    @NonNull
    private Set<String> StringToStringSet(@Nullable String setString) {
        if (setString == null) {
            return new HashSet<>();
        }

        return new HashSet<>(Arrays.asList(setString.split(DELIMITER)));
    }
}
