package com.marktony.translator.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marktony.translator.R;
import com.marktony.translator.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.translator.model.MeetingItem;

import java.util.ArrayList;

/**
 * Created by lemonhuang on 2017/5/3.
 */

public class MeetingItemAdapter extends RecyclerView.Adapter<MeetingItemAdapter.MeetingItemViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<MeetingItem> list;
    private OnRecyclerViewOnClickListener mListener;
    public MeetingItemAdapter(@NonNull Context context, ArrayList<MeetingItem> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public MeetingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MeetingItemViewHolder(inflater.inflate(R.layout.meeting_card_item,parent,false),mListener);
    }

    @Override
    public void onBindViewHolder(MeetingItemViewHolder holder, int position) {
        MeetingItem meetingItem = list.get(position);
        holder.tvTitle.setText(meetingItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    public class MeetingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvTitle;
        ImageView ivEdit;

        OnRecyclerViewOnClickListener listener;
        public MeetingItemViewHolder(View itemView, final OnRecyclerViewOnClickListener listener){
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.text_view_title);
            ivEdit = (ImageView) itemView.findViewById(R.id.image_view_edit);

            this.listener = listener;
            itemView.setOnClickListener(this);

            tvTitle.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    listener.OnSubViewClick(tvTitle,getLayoutPosition());
                }
            });

            ivEdit.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    listener.OnSubViewClick(ivEdit,getLayoutPosition());
                }
            });

        }
        @Override
        public void onClick(View v) {
            if(listener!=null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }
}
