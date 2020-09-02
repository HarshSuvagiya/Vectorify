package com.scorpion.vectorial.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scorpion.vectorial.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterGradients extends RecyclerView.Adapter<AdapterGradients.ViewHolder> {
    private List<GradientDrawable> gradientDrawables;
    public GradientsInterface gradientsInterface;

    public interface GradientsInterface {
        void onClick(GradientDrawable gradientDrawable);
    }

    public AdapterGradients(List<GradientDrawable> list) {
        this.gradientDrawables = list;
    }

    public void setGradientsInterface(GradientsInterface gradientsInterface2) {
        this.gradientsInterface = gradientsInterface2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_gradient, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final GradientDrawable gradientDrawable = this.gradientDrawables.get(i);
        viewHolder.linearLayout.setBackground(gradientDrawable);
        if (this.gradientsInterface != null) {
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AdapterGradients.this.gradientsInterface.onClick(gradientDrawable);
                }
            });
        }
    }

    public int getItemCount() {
        return this.gradientDrawables.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.card_gradient_background);
        }
    }
}
