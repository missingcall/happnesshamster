package com.kissspace.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;

import com.kissspace.module_common.R;


public class ShowProgressDialog {

    public Dialog dialog;
    private boolean canBackPressed = true;

    // 展示进度对话框，自定义要用动画
    public void show(final Context context) {
        dialog = new Dialog(context, R.style.progressDialog) {
            @Override
            public void onBackPressed() {
                if (canBackPressed) {
                    super.onBackPressed();
                }
            }
        };
        dialog.setContentView(R.layout.common_dialog_room_loading);
        dialog.setCanceledOnTouchOutside(false);

       DialogInterface.OnKeyListener keyListener = (dialogInterface, keyCode, keyEvent) -> {
           if (keyCode== KeyEvent.KEYCODE_BACK&&keyEvent.getRepeatCount()==0)
           {
               ((Activity)context).finish();
               return true;
           }
           else
           {
               return false;
           }
       };
        dialog.setOnKeyListener(keyListener);
        dialog.setCancelable(false);
        // ProgressBar progress = (ProgressBar)dialog.findViewById(R.id.image);
        dialog.show();
    }


    public void showDialog(Context context,String content) {
        if(dialog!=null&&dialog.isShowing()){
            if(((TextView) dialog.findViewById(R.id.tv_loading)).getText() == content){
                return;
            }else {
                dialog.dismiss();
            }
        }
        dialog = new Dialog(context, R.style.Theme_CustomDialogFragment) {
            @Override
            public void onBackPressed() {
                if (canBackPressed) {
                    super.onBackPressed();
                }
            }
        };
        dialog.setContentView(R.layout.common_dialog_room_loading);
        dialog.setCanceledOnTouchOutside(false);
        ((TextView) dialog.findViewById(R.id.tv_loading)).setText(content);
        dialog.show();
    }



    public void cancel() {
        try {
            if (dialog!=null&&dialog.isShowing()){
                dialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss()
    {
        try {
            if(dialog != null)
            {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            dialog = null;
        }
    }
}