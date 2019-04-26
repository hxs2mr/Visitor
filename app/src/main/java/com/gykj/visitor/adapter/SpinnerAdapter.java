package com.gykj.visitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gykj.visitor.R;
import com.gykj.visitor.entity.TeacherEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/2014:59
 * version: 1.0
 */
public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<TeacherEntity> mDatas;

    public SpinnerAdapter(Context context, List<TeacherEntity> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    public void setDatas(List<TeacherEntity> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == mDatas)
            return 0;
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int size = mDatas.size();
        holder.mTv.setText(mDatas.get(i).getTeacherName());
        return view;
    }

    class ViewHolder {
        @BindView(R.id.epay_spinner_item_tv)
        TextView mTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}