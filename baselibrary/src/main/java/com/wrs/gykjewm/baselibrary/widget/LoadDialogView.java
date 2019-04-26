package com.wrs.gykjewm.baselibrary.widget;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wrs.gykjewm.baselibrary.R;

/**
 * description:加载框
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午1:46
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class LoadDialogView {

    private static Dialog dialog;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void showDialog(Context context, String message) {
        //防止快速显示,新对象覆盖旧对象,导致旧对象的关闭不了
        if (dialog != null) {
            if (dialog.isShowing()) {
                dismssDialog();
            }
        }
        /*dialogView = ResourcesUtils.findViewById(context, R.layout.load_dialog);
        TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_dialog_msg);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
        dialog = new AlertDialog.Builder(context, R.style.Dialog).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setContentView(dialogView);*/

        dialog = new MaterialDialog.Builder(context)
                    .content(message)
                    .backgroundColorRes(R.color.color_ffffff)
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .canceledOnTouchOutside(false)
                    .show();
    }

    public static void dismssDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
