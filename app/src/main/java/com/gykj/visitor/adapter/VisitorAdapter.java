package com.gykj.visitor.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.VisitorListEntity;

import java.util.List;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/512:39
 * version: 1.0
 */
public class VisitorAdapter extends BaseQuickAdapter<VisitorListEntity,BaseViewHolder> {


    public VisitorAdapter(@Nullable List<VisitorListEntity> data) {
        super(R.layout.layout_visitor_list_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VisitorListEntity item) {
        helper.setText(R.id.item_visitor_number_tv,String.valueOf(helper.getAdapterPosition()+1));
        helper.setText(R.id.item_visitor_name_tv,item.getName());
        helper.setText(R.id.item_visitor_phone_tv,item.getPhone());
        helper.setText(R.id.item_visitor_time_tv,item.getIntoSchoolTime());
        helper.addOnClickListener(R.id.item_visitor_operate_tv);
    }
}
