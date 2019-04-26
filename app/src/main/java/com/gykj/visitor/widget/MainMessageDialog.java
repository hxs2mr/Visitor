package com.gykj.visitor.widget;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.dialog.widget.base.BaseDialog;
import com.gykj.visitor.R;
import com.gykj.visitor.ui.activity.MainActivity;
import com.gykj.visitor.ui.activity.VisitorListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * name : HXS
 * e-mail : 1363826037@qq.com
 * descript:借用登记dialog
 * date   : 2019/3/1214:19
 * version: 1.0
 */
public class MainMessageDialog extends BaseDialog<MainMessageDialog> {

    @BindView(R.id.rel_teach_msg)
    RelativeLayout rel_teach_msg; //老师回复的消息

    @BindView(R.id.rel_no_sc)
    RelativeLayout rel_no_sc; //未离校人员



    private MainActivity mainActivity;
    public MainMessageDialog(Context context, Activity activity) {
        super(context);
        this.mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        View inflate = View.inflate(mContext, R.layout.dialog_main_message, null);
        ButterKnife.bind(this,inflate);
        return  inflate;
    }

    @Override
    public void setUiBeforShow() {
        rel_teach_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        rel_no_sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showActivity(mainActivity, VisitorListActivity.class);
                dismiss();
            }
        });

    }
}
