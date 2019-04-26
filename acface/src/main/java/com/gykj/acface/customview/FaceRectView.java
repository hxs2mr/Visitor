package com.gykj.acface.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.gykj.acface.model.DrawInfo;
import com.gykj.acface.util.DrawHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceRectView extends View {
    private CopyOnWriteArrayList<DrawInfo> faceRectList = new CopyOnWriteArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRectList != null && faceRectList.size() > 0) {
            for (int i = 0; i < faceRectList.size(); i++) {
//                if (i >= 4) {
//                    break;
//                }
            DrawHelper.drawFaceRect(canvas, faceRectList.get(i), Color.parseColor("#f2f0f0"), 2);
            }
        }
    }
    public void clearFaceInfo() {
        faceRectList.clear();
        // postInvalidate();
        invalidate();
    }

    public void addFaceInfo(DrawInfo faceInfo) {
        faceRectList.add(faceInfo);
         postInvalidate();
       // invalidate();
    }

    public void addFaceInfo(List<DrawInfo> faceInfoList) {
        faceRectList.addAll(faceInfoList);
       // postInvalidate();
        invalidate();
    }
}