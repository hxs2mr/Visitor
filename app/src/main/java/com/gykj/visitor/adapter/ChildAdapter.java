package com.gykj.visitor.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.ChildEntity;

import java.util.List;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/219:50
 * version: 1.0
 */
public class ChildAdapter extends BaseQuickAdapter<ChildEntity,BaseViewHolder> {

    private Context mContext;

    public ChildAdapter(Context context,@Nullable List<ChildEntity> data) {
        super(R.layout.layout_child_item,data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ChildEntity item) {
        helper.setText(R.id.item_name_tv,mContext.getResources().getString(R.string.result_child_name)+item.getStudntName());
        helper.setText(R.id.item_grade_tv,mContext.getResources().getString(R.string.result_child_grade)+item.getGradeName());
        helper.setText(R.id.item_class_tv,mContext.getResources().getString(R.string.result_child_class)+item.getClassName());
    }
}
