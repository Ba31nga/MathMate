package com.example.mathmate.Utils;

import static com.example.mathmate.Utils.NotesUtil.errorMessage;
import static com.example.mathmate.Utils.NotesUtil.successMessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.mathmate.R;

// Custom BroadcastReceiver to listen for network connectivity changes
public class NetworkChangeReceiver extends BroadcastReceiver {

    private Dialog noInternetDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the broadcasted intent action is for connectivity changes
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // Determine if the device is connected to the internet
            boolean isConnected = isNetworkAvailable(context);
            if (!isConnected) {
                showDialog(context);
            } else {
                dismissDialog();
            }
        }
    }

    // Method to check if the device is currently connected to the internet
    private boolean isNetworkAvailable(Context context) {
        // Get the connectivity manager service
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // Get the currently active network
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            // Return true if the active network is connected, otherwise false
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void showDialog(Context context) {
        if (noInternetDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(R.layout.dialog);
            builder.setCancelable(false); // Prevent dialog dismissal on outside touch or back press
            noInternetDialog = builder.create();
        }
        if (!noInternetDialog.isShowing()) {
            noInternetDialog.show();
        }
    }

    private void dismissDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }
}