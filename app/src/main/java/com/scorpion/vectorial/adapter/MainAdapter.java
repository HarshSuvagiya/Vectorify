package com.scorpion.vectorial.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scorpion.vectorial.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    ArrayList<Integer> myList;
    Context mContext;
    IconListener iconListener;

    public MainAdapter(ArrayList<Integer> myList, Context mContext, IconListener iconListener1) {
        this.myList = myList;
        this.mContext = mContext;
        this.iconListener = iconListener1;
    }

    public interface IconListener{
        void onClickIcon(int pos);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.image.setImageResource(myList.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconListener.onClickIcon(myList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
        }
    }
}
