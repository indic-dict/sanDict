
package com.annie.dictionary.frags;

import com.annie.dictionary.DictSpeechEng;
import com.annie.dictionary.MainActivity;
import com.annie.dictionary.R;
import com.annie.dictionary.service.QDictService;
import com.annie.dictionary.standout.StandOutWindow;
import com.annie.dictionary.utils.Utils;
import com.annie.dictionary.utils.Utils.Def;
import com.annie.dictionary.utils.Utils.RECV_UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingFragment extends PreferenceFragment implements Def, OnPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;

    private Preference mPrefSource;

    private ListPreference mFontPreference, mThemePreference, mMaxFavPreference;

    private CheckBoxPreference mTTSPref, mNotifPref, mUseCapture;

    Typeface mFont;

    private int mCurrentFontIndex = 0;

    private int mCurrentThemeIndex = 0;

    public SettingFragment() {
        // default constructor
    }

    public SettingFragment(DictSpeechEng speechEng) {
        mSpeechEng = speechEng;
    }

    public void SetSpeechEng(DictSpeechEng speechEng) {
        mSpeechEng = speechEng;
    }

    private void reset() {
        Editor edit = mSharedPreferences.edit();
        edit.putString(PREF_INDEX_CHECKED, "");
        edit.putString(getString(R.string.prefs_key_font_text), DEFAULT_FONT);
        edit.apply();
        mFontPreference.setValueIndex(2);
        onPreferenceChange(mFontPreference, DEFAULT_FONT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        addPreferencesFromResource(R.xml.prefs_settings);
        mSharedPreferences = activity.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        mPrefSource = findPreference(getString(R.string.prefs_key_source));
        mFontPreference = (ListPreference)findPreference(getString(R.string.prefs_key_font_text));
        mFontPreference.setOnPreferenceChangeListener(this);
        mThemePreference = (ListPreference)findPreference(getString(R.string.prefs_key_theme));
        mThemePreference.setOnPreferenceChangeListener(this);
        mMaxFavPreference = (ListPreference)findPreference(getString(R.string.prefs_key_max_recent_word));
        mMaxFavPreference.setOnPreferenceChangeListener(this);
        mTTSPref = (CheckBoxPreference)findPreference(getResources().getString(R.string.prefs_key_using_tts));
        mTTSPref.setOnPreferenceChangeListener(this);
        mNotifPref = (CheckBoxPreference)findPreference(
                getResources().getString(R.string.prefs_key_capture_notification));
        mNotifPref.setOnPreferenceChangeListener(this);
        mUseCapture = (CheckBoxPreference)findPreference(getString(R.string.prefs_key_using_capture));
        mUseCapture.setOnPreferenceChangeListener(this);
        initInfo();
    }

    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        String s = mSharedPreferences.getString(Def.PREF_KEY_FONT, Def.DEFAULT_FONT);
        mFont = Utils.getFont(activity, s);
        mCurrentFontIndex = mFontPreference.findIndexOfValue(s);

        mCurrentThemeIndex = mSharedPreferences.getInt("prefs_key_theme", 0);
        applyFont(mFont);
        return view;
    }

    private void applyFont(Typeface font) {
        convertPreferenceToUseCustomFont(mPrefSource);
        convertPreferenceToUseCustomFont(mFontPreference);
        convertPreferenceToUseCustomFont(mThemePreference);
        convertPreferenceToUseCustomFont(mTTSPref);
        convertPreferenceToUseCustomFont(mNotifPref);
        convertPreferenceToUseCustomFont(mUseCapture);
    }

    private void initInfo() {
        if (mSpeechEng == null) {
            mSpeechEng = DictSpeechEng.getInstance(activity.getApplicationContext());
        }
        if (TextUtils.isEmpty(mFontPreference.getEntry())) {
            mFontPreference.setValueIndex(3);
        }
        boolean tts = mSharedPreferences.getBoolean(getResources().getString(R.string.prefs_key_using_tts), true);
        if (TextUtils.isEmpty(mMaxFavPreference.getEntry())) {
            mMaxFavPreference.setValueIndex(0);
        }
        if (mSpeechEng.isCanSpeak()) {
            mTTSPref.setEnabled(true);
            mTTSPref.setChecked(tts);
        } else {
            mTTSPref.setEnabled(false);
            mTTSPref.setChecked(false);
        }
        mPrefSource.setSummary(Utils.getRootFolder());
        mFontPreference.setSummary(mFontPreference.getEntry());
        mThemePreference.setSummary(mThemePreference.getEntry());
        mMaxFavPreference.setSummary(
                getString(R.string.prefs_title_max_favorite_word_summary, mMaxFavPreference.getEntry().toString()));
        // service
        mUseCapture.setChecked(mSharedPreferences.getBoolean(getString(R.string.prefs_key_using_capture), false));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFontPreference) {
            int index = mFontPreference.findIndexOfValue(newValue.toString());
            if (index != mCurrentFontIndex) {
                CharSequence[] entries = mFontPreference.getEntries();
                mFontPreference.setSummary(entries[index]);
                mSharedPreferences.edit().putString(PREF_KEY_FONT, newValue.toString()).apply();
                Intent intent = new Intent(MainActivity.ACTION_UPDATE_UI);
                intent.putExtra(MainActivity.ACTION_UPDATE_KEY, RECV_UI.CHANGE_FONT);
                activity.sendBroadcast(intent);
                activity.finish();
            }
            return true;
        } else if (preference == mThemePreference) {
            int index = mThemePreference.findIndexOfValue(newValue.toString());
            if (mCurrentThemeIndex != index) {
                mSharedPreferences.edit().putInt("prefs_key_theme", index).apply();
                Intent intent = new Intent(MainActivity.ACTION_UPDATE_UI);
                intent.putExtra(MainActivity.ACTION_UPDATE_KEY, RECV_UI.CHANGE_THEME);
                intent.putExtra("receiver_theme_index", index);
                activity.sendBroadcast(intent);
                activity.finish();
            }
            return true;
        } else if (preference == mTTSPref) {
            mSharedPreferences.edit()
                    .putBoolean(getResources().getString(R.string.prefs_key_using_tts), (Boolean)newValue).apply();
            mTTSPref.setChecked((Boolean)newValue);
            return true;
        } else if (preference == mMaxFavPreference) {
            int index = mMaxFavPreference.findIndexOfValue(newValue.toString());
            CharSequence[] entries = mMaxFavPreference.getEntryValues();
            int maxFav = Integer.valueOf(entries[index].toString());
            mSharedPreferences.edit().putInt("prefs_key_max_recent_word", maxFav).apply();
            mMaxFavPreference
                    .setSummary(getString(R.string.prefs_title_max_favorite_word_summary, entries[index].toString()));
            return true;
        } else if (preference == mNotifPref) {
            mSharedPreferences.edit().putBoolean("prefs_key_capture_notification", (Boolean)newValue).apply();
            mNotifPref.setChecked((Boolean)newValue);
            if (QDictService.RUNNING) {
                StandOutWindow.toggleNoti(getActivity(), QDictService.class, StandOutWindow.DEFAULT_ID,
                        (Boolean)newValue);
            }
            return true;
        } else if (preference == mUseCapture) {
            if (newValue instanceof Boolean) {
                Boolean boolVal = (Boolean)newValue;
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(getString(R.string.prefs_key_using_capture), boolVal);
                editor.apply();
                Intent intent = new Intent(MainActivity.ACTION_UPDATE_UI);
                intent.putExtra("receiver_update_ui", RECV_UI.RUN_SERVICE);
                getActivity().sendBroadcast(intent);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void questionResetDlg() {
        AlertDialog.Builder alertDialogBuilder = null;
        if (Utils.hasHcAbove()) {
            alertDialogBuilder = new AlertDialog.Builder(activity, R.style.QDialog);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(activity);
        }
        alertDialogBuilder.setMessage(R.string.app_name);
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void convertPreferenceToUseCustomFont(Preference somePreference) {
        CustomTypefaceSpan customTypefaceSpan = new CustomTypefaceSpan("", mFont);
        SpannableStringBuilder ss;
        if (somePreference.getTitle() != null) {
            ss = new SpannableStringBuilder(somePreference.getTitle().toString());
            ss.setSpan(customTypefaceSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            somePreference.setTitle(ss);
        }

        if (somePreference.getSummary() != null) {
            ss = new SpannableStringBuilder(somePreference.getSummary().toString());
            ss.setSpan(customTypefaceSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            somePreference.setSummary(ss);
        }
    }

    static private class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }

    private FragmentActivity activity;

    private DictSpeechEng mSpeechEng;
}
