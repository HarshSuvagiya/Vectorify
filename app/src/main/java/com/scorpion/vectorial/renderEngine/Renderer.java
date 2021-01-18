package com.scorpion.vectorial.renderEngine;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;

public class Renderer extends RenderEngine {

    private static final String TAG = "Renderer";

    public LottieAnimationView animationView, backgroundAnimationView;
    public Context context;
    public RelativeLayout constraintLayout;
    String str = TAG;
    int videoFrames;

    public Renderer(Context context, RelativeLayout constraintLayout, LottieAnimationView lottieAnimationView, LottieAnimationView backgroundAnimationView, int videoFrames) {

        this.context = context;
        this.constraintLayout = constraintLayout;
        this.animationView = lottieAnimationView;
        this.videoFrames = videoFrames;
        this.backgroundAnimationView = backgroundAnimationView;

    }

    public void init(final File file,int height1,int width1) {

//        setResolution(constraintLayout.getHeight(), constraintLayout.getWidth());
        setResolution(height1, width1);
        Log.e("RESHeight", String.valueOf(constraintLayout.getHeight()));
        Log.e("RESWidth", String.valueOf(constraintLayout.getWidth()));
        try {
            generateMovie(file);

        } catch (Exception e) {


            float height = constraintLayout.getWidth() % 2;
            int heightnew = constraintLayout.getHeight();
            int widthnew = constraintLayout.getWidth();
            if (height != 0) {
                heightnew = heightnew - 1;
            } else {
                widthnew = widthnew - 1;
            }
            try {
//                setFrameResolution(heightnew, widthnew);
                setFrameResolution(heightnew, widthnew);
                generateMovie(file);
            } catch (Exception e1) {
                interfaceRenderEngine.onRenderFailed(e1.getMessage());
                showAlertDialog("Rendering error", constraintLayout.getHeight() + "= height " + constraintLayout.getWidth() + "= width");
            }
            Log.e(str, "Movie generation FAILED", e);

        }
    }

    public void showAlertDialog(String str, String str2) {
        Builder builder = new Builder(this.context);
        builder.setTitle(str);
        builder.setMessage(str2);
        builder.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void generateMovie(final File file) {

        setLayout(constraintLayout);

        try {
            prepareEncoder(file);
            final Handler handler = new Handler();
            handler.post(new Runnable() {

                int count = 0;

                public void run() {
                    drainEncoder(false);

                    animationView.setFrame((int) (this.count % animationView.getMaxFrame()));
                    if (backgroundAnimationView != null) {
                        backgroundAnimationView.setFrame((int) (this.count % animationView.getMaxFrame()));
                    }

                    generateFrame(renderer(constraintLayout));
                    int i = this.count;
                    this.count = i + 1;

                    if (((float) i) < videoFrames) {
                        handler.postDelayed(this, 0);
                        float calcFramePercentage = calcFramePercentage(this.count, videoFrames);
                        if (interfaceRenderEngine != null) {
                            interfaceRenderEngine.onProgressChange(calcFramePercentage);
                            return;
                        }
                        return;
                    }
                    drainEncoder(true);
                    releaseEncoder();
                    if (interfaceRenderEngine != null) {
                        interfaceRenderEngine.onRendered(file);
                    }
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "generateMovie: ", e);
        }
    }

    public void setFrameResolution(int heightNew, int widthNew) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
        layoutParams.width = widthNew;
        layoutParams.height = heightNew;
        constraintLayout.setLayoutParams(layoutParams);

    }
}
