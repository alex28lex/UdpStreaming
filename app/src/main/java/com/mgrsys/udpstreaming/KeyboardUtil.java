package com.mgrsys.udpstreaming;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


@SuppressWarnings("unused")
public final class KeyboardUtil {

    public static void showKeyboard(@NonNull View editTextView) {
        editTextView.requestFocus();
        Context context = editTextView.getContext();
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(@NonNull View anchorView) {
        Context context = anchorView.getContext();
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder windowToken = anchorView.getWindowToken();
        inputMethodManager.hideSoftInputFromWindow(windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void preventShowingKeyboard(@NonNull Activity activity) {
        activity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
