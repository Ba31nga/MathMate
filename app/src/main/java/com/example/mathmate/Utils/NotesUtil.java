package com.example.mathmate.Utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;

import com.example.mathmate.ContinueAddForumActivity;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class NotesUtil {

    public static void successMessage(Context context, String message) {
        MotionToast.Companion.darkToast((Activity) context,
                "Success!",
                message,
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular));
    }

    public static void errorMessage(Context context, String message) {
        MotionToast.Companion.darkToast((Activity) context,
                "Failure!",
                message,
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular));
    }



}
