package edu.csulb.android.photonotes.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.csulb.android.photonotes.R;

/**
 * Created by vaibhavjain on 3/5/2017.
 */

public class CustomToast {
    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG = 1;
    public static Toast getToast(Context ctx, String message, int duration){
        Toast toast = Toast.makeText(ctx, message, duration);
        View toastView = toast.getView();
        //toastView.setBackground(ContextCompat.getDrawable(ctx, android.R.drawable.toast_frame));
        toastView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary));
        toastView.setPadding(30,30,30,30);
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextColor(ContextCompat.getColor(ctx, R.color.white));
        return toast;
    }
}
