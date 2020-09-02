package com.scorpion.vectorial.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scorpion.vectorial.AdUtils.FBInterstitial;
import com.scorpion.vectorial.Helper;
import com.scorpion.vectorial.R;
import com.scorpion.vectorial.activity.MainActivity;
import com.scorpion.vectorial.activity.StartActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.MyViewHolder> {

    ArrayList<String> myList;
    Context mContext;
    CatListListener catListListener;

    public CatAdapter(ArrayList<String> myList, Context mContext,CatListListener catListListener1) {
        this.myList = myList;
        this.mContext = mContext;
        catListListener = catListListener1;
    }

    public interface CatListListener{
        void catClickListener(String name);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cat_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.title.setText(myList.get(position));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.adcount++;
                if(Helper.adcount%3==0)
                {
                    FBInterstitial.getInstance().displayFBInterstitial(mContext, new FBInterstitial.FbCallback() {
                        public void callbackCall() {
                            catListListener.catClickListener(myList.get(position));
                        }
                    });
                }
                else
                {
                    catListListener.catClickListener(myList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout mainLayout;
        TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            title = itemView.findViewById(R.id.title);
        }
    }
}
