package com.scorpion.vectorial;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Gradients {
    private String TAG = "from_gradients";
    private Context context;
    private List<GradientDrawable> gradients = new ArrayList();

    public Gradients(Context context2) {
        this.context = context2;
    }

    private void init() {
        try {
            JSONArray jSONArray = new JSONArray(loadJSONFromRaw(R.raw.gradients));
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONArray jSONArray2 = jSONArray.getJSONObject(i).getJSONArray("colors");
                int[] iArr = new int[jSONArray2.length()];
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    iArr[i2] = Color.parseColor(jSONArray2.getString(i2));
                }
                this.gradients.add(new GradientDrawable(GradientDrawable.Orientation.TL_BR, iArr));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            String str = this.TAG;
            Log.i(str, "setGradient: " + e.getMessage());
        }
    }

    public List<GradientDrawable> getGradients() {
        init();
        return this.gradients;
    }

    public String loadJSONFromRaw(int i) {
        try {
            InputStream openRawResource = this.context.getResources().openRawResource(i);
            byte[] bArr = new byte[openRawResource.available()];
            openRawResource.read(bArr);
            openRawResource.close();
            return new String(bArr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
