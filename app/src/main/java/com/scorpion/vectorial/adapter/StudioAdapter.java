package com.scorpion.vectorial.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.scorpion.vectorial.R;
import com.scorpion.vectorial.activity.ImageViewActivity;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class StudioAdapter extends RecyclerView.Adapter<StudioAdapter.MyViewHolder> {

    ArrayList<File> myList;
    Context mContext;

    public StudioAdapter(ArrayList<File> myList, Context mContext) {
        this.myList = myList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_in_one_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(20));
        Glide.with(mContext).load(myList.get(position).getAbsolutePath())
                .centerCrop().apply(requestOptions).transition(withCrossFade())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra("imageUrl", myList.get(position).getAbsolutePath());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ConstraintLayout ll;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
