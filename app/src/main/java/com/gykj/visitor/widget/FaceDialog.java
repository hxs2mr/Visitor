package com.gykj.visitor.widget;

import android.app.Activity;
import android.content.Context;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.dialog.widget.base.BaseDialog;
import com.gykj.acface.customview.FaceRectView;
import com.gykj.visitor.R;
import com.gykj.visitor.ui.activity.FaceAuthActivity;
import com.gykj.visitor.ui.activity.MainActivity;
import com.gykj.visitor.ui.activity.VisitorListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * name : HXS
 * e-mail : 1363826037@qq.com
 * descript:人脸对比Dialog
 * date   : 2019/3/1214:19
 * version: 1.0
 */
public class FaceDialog extends BaseDialog<FaceDialog> {

    @BindView(R.id.textureview_preview)
    TextureView mFacePreView;

    @BindView(R.id.facerect_view)
    FaceRectView mFaceRectView;

    @BindView(R.id.face_circle_iv)
    ImageView mFaceCircleIv;

    private MainActivity mainActivity;
    public FaceDialog(Context context, Activity activity) {
        super(context);
        this.mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        setCancelable(false);
        View inflate = View.inflate(mContext, R.layout.dialog_face, null);
        ButterKnife.bind(this,inflate);
        return  inflate;
    }

    @Override
    public void setUiBeforShow() {



    }
}
