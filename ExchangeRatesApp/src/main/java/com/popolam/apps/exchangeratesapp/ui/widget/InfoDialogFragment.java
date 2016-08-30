package com.popolam.apps.exchangeratesapp.ui.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by user on 08.11.13.
 */
public class InfoDialogFragment extends DialogFragment {

    private OnDialogListener onDialogListener;

    public interface OnDialogListener {
        void onDialogResult(int result, Bundle data);
    }

    public static InfoDialogFragment newInstance(String title, String message, String okButtonText) {
        return newInstance(title, message, okButtonText, null);
    }

    public static InfoDialogFragment newInstance(String title, String message, String okButtonText, String cancelButtonText) {
        InfoDialogFragment fr = new InfoDialogFragment();
        Bundle args = new Bundle(2);
        args.putString("title", title);
        args.putString("message", message);
        args.putString("ok", okButtonText);
        args.putString("cancel", cancelButtonText);
        fr.setArguments(args);
        return fr;
    }

    public void setOnDialogListener(OnDialogListener listener){
        this.onDialogListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String okButtonText = getArguments().getString("ok");
        String cancelButtonText = getArguments().getString("cancel");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"))
                .setCancelable(true)
                .setMessage(getArguments().getString("message"))
                .setPositiveButton(okButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onDialogListener != null) {
                            onDialogListener.onDialogResult(Activity.RESULT_OK, null);
                        }
                    }
                });
        if (cancelButtonText != null) {
            builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onDialogListener != null) {
                        onDialogListener.onDialogResult(Activity.RESULT_CANCELED, null);
                    }
                }
            });
        }

        return builder.create();
    }
}
