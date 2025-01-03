package com.navigationdemo.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navigationdemo.R;

import androidx.annotation.NonNull;

import com.navigationdemo.Chat_Msg;

import com.navigationdemo.utils.MyChatHead_ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ChatMsgAdapter extends ArrayAdapter {
    private List<Chat_Msg> mData;
    private Context mContext;
    private int resourceId;

    public ChatMsgAdapter(@NonNull Context context, int resource, @NonNull List objects, OnClearDataListener listener) {
        super(context, resource, objects);
        this.mContext = context;
        this.resourceId = resource;
        this.mData = objects;
        mListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    // 定义回调接口
    public interface OnClearDataListener {
        void onClearData();  // 清除数据的方法
    }

    private OnClearDataListener mListener;  // 声明接口对象


    public View getView(int position, View convertView, ViewGroup parent) {
        if (mData == null || mData.isEmpty()) {
            // 如果数据为空，返回一个占位视图（可以是一个空的布局）
            return new View(mContext);
        }
        Chat_Msg mMsg = mData.get(position);
        getItem(position);
        View mView;
        ViewHolder mViewHolder;
        if (convertView == null) {
            mView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            mViewHolder = new ViewHolder();
            mViewHolder.Layout = (LinearLayout) mView.findViewById(R.id.msg);
            mViewHolder.Layout1 = (LinearLayout) mView.findViewById(R.id.sender);
            mViewHolder.Msgname = (TextView) mView.findViewById(R.id.name);
            mViewHolder.Msgdate = (TextView) mView.findViewById(R.id.date);
            mViewHolder.Msgcontent = (TextView) mView.findViewById(R.id.msgcontent);
            mViewHolder.msg_head = mView.findViewById(R.id.msg_head);

            mView.setTag(mViewHolder);
        } else {
            mView = convertView;
            mViewHolder = (ViewHolder) mView.getTag();
        }
        mViewHolder.Msgname.setText(mMsg.getName());
        mViewHolder.Msgdate.setText(mMsg.getDate());
        mViewHolder.Msgcontent.setText(mMsg.getContent());
        //设置格式
        if (mMsg.getType() == 2)      //  接受  听课端的
        {
            if (mMsg.getContent().equals("clearAllMsgFromTeacher")){
                // 清屏
                if (mListener != null) {
                    mListener.onClearData();  // 调用回调方法，通知 ChatRoomFragmentStu 清除数据
                    return new View(mContext);
                }
            }else {
                mViewHolder.Layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                mViewHolder.Layout1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                // mViewHolder.msg_head.setBackgroundResource(R.mipmap.teachathead);
                ImageLoader.getInstance().displayImage(mMsg.getUrl(), mViewHolder.msg_head);
            }
        }
        else if (mMsg.getType() == 1)  //  发送的消息  主讲人
        {
            mViewHolder.Layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            mViewHolder.Layout1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            mViewHolder.Msgname.setTextColor(Color.WHITE);
            mViewHolder.Msgdate.setTextColor(Color.WHITE);
            mViewHolder.Msgcontent.setTextColor(Color.WHITE);
//            mViewHolder.msg_head.setBackgroundResource(R.mipmap.stuchathead);
            ImageLoader.getInstance().displayImage(mMsg.getUrl(), mViewHolder.msg_head);
        }
        return mView;
    }

    class ViewHolder {
        LinearLayout Layout;
        LinearLayout Layout1;
        TextView Msgname;
        TextView Msgdate;
        TextView Msgcontent;
        ImageView msg_head;
    }

}
