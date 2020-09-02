package com.scorpion.vectorial.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.scorpion.vectorial.AdUtils.FBInterstitial;
import com.scorpion.vectorial.Gradients;
import com.scorpion.vectorial.Helper;
import com.scorpion.vectorial.R;
import com.scorpion.vectorial.adapter.AdapterGradients;
import com.scorpion.vectorial.adapter.CatAdapter;
import com.scorpion.vectorial.adapter.MainAdapter;
import com.scorpion.vectorial.color.AmbilWarnaDialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainAdapter.IconListener , CatAdapter.CatListListener{

    ArrayList<Integer> icons = new ArrayList<>();
    ArrayList<String> catList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView catRecycler;
    MainAdapter mainAdapter;
    ImageView foreground;
    ImageView background,foregroundColorBtn, backgroundColorBtn;
    ImageView done;
    Button categories;
    ImageView gradient;
    int foregroundItem, backgroundColor = Color.BLACK, tintColor = Color.WHITE;
    Dialog catDialog;
    boolean isGradientApplied = false;
    public static GradientDrawable gradientDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Helper.isPermissionGranted(MainActivity.this)) {
            init();
        } else {
            Helper.requestPermission(MainActivity.this, 101);
        }

        init();

        foregroundItem = R.drawable.monuments_chiang_kai_shek_memorial_hall_taipei_taiwan;
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(MainActivity.this, R.drawable.monuments_chiang_kai_shek_memorial_hall_taipei_taiwan);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, tintColor);
        foreground.setImageResource(R.drawable.monuments_chiang_kai_shek_memorial_hall_taipei_taiwan);

        loadDrawables(R.drawable.class, "monuments");
        mainAdapter = new MainAdapter(icons, MainActivity.this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(mainAdapter);

        buttonClick();
        getCatList();

        RecyclerView catRecycler = findViewById(R.id.catRecycler);
        catRecycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        catRecycler.setAdapter(new CatAdapter(catList,MainActivity.this,MainActivity.this));

    }

    private void getCatList() {
        catList.add("Monument");
        catList.add("Love");
        catList.add("Travel");
        catList.add("Bakery");
        catList.add("Internet Security");
        catList.add("Camping");
        catList.add("Social Media");
        catList.add("Birthday");
        catList.add("Business");
        catList.add("Spring");
        catList.add("Location");
        catList.add("Sweets and Candies");
        catList.add("Gym");
        catList.add("Public Transport");
        catList.add("Carnival");
        catList.add("Cinema");
        catList.add("Vehicles Transport");
        catList.add("Emergencies");
        catList.add("Space");
        catList.add("Music");
        catList.add("Nature");
        catList.add("Stay at Home");
        catList.add("Zodiac");
    }

    private void buttonClick() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBInterstitial.getInstance().displayFBInterstitial(MainActivity.this, new FBInterstitial.FbCallback() {
                    public void callbackCall() {
                        startActivity(new Intent(getApplicationContext(), EditorActivity.class)
                                .putExtra("background", backgroundColor)
                                .putExtra("foreground", foregroundItem)
                                .putExtra("isGradientApplied", isGradientApplied)
                                .putExtra("tint", tintColor));
                    }
                });


            }
        });

        gradient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGradient();
            }
        });

        foregroundColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialogForForeground();
            }
        });

        backgroundColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialogForBackground();
            }
        });

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catDialog = new Dialog(MainActivity.this);
                catDialog.setContentView(R.layout.category_dialog);
                catDialog.show();
                catDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                RecyclerView catRecycler = catDialog.findViewById(R.id.catRecycler);
                catRecycler.setAdapter(new CatAdapter(catList,MainActivity.this,MainActivity.this));
            }
        });
    }
    public void loadBannerFirst(Activity act, LinearLayout laybanner){

        //banner ad
        com.facebook.ads.AdView adViewfb = new com.facebook.ads.AdView(act, getString(R.string.banner_ad_unit_id), AdSize.BANNER_HEIGHT_50);

        // Add the ad view to your activity layout
        laybanner.addView(adViewfb);
        // Request an ad
        adViewfb.loadAd();
    }

    private void setGradient() {
        final Dialog gradientDialog = new Dialog(MainActivity.this);
        gradientDialog.setContentView(R.layout.bottom_sheet_gradient);
        Objects.requireNonNull(gradientDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        gradientDialog.show();
        RecyclerView rvGradient = gradientDialog.findViewById(R.id.rv_gradients);
        ImageView gbsCancelButton = gradientDialog.findViewById(R.id.gbsCancelButton);

        LinearLayout adContainer = (LinearLayout) gradientDialog.findViewById(R.id.banner_container);
        loadBannerFirst(MainActivity.this,adContainer);

        AdapterGradients adapterGradients = new AdapterGradients(new Gradients(MainActivity.this).getGradients());
        rvGradient.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        rvGradient.setAdapter(adapterGradients);
        adapterGradients.setGradientsInterface(new AdapterGradients.GradientsInterface() {
            public void onClick(GradientDrawable gradientDrawable1) {
                gradientDrawable = gradientDrawable1;
                background.setBackground(gradientDrawable);
                gradientDialog.dismiss();
                isGradientApplied = true;
            }
        });

        gbsCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradientDialog.dismiss();
            }
        });
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        foreground = findViewById(R.id.foreground);
        background = findViewById(R.id.background);
        gradient = findViewById(R.id.gradient);
        done = findViewById(R.id.done);
        categories = findViewById(R.id.categories);
        foregroundColorBtn = findViewById(R.id.foregroundColorBtn);
        backgroundColorBtn = findViewById(R.id.backgroundColorBtn);
        catRecycler = findViewById(R.id.catRecycler);
    }

    private void showColorDialogForBackground() {

        AmbilWarnaDialog textColorDialog = new AmbilWarnaDialog(this, false, backgroundColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        dialog.getDialog().dismiss();
                        backgroundColor = color;
                        background.setBackgroundColor(backgroundColor);
                        isGradientApplied = false;
                    }
                });
        textColorDialog.show();

    }

    private void showColorDialogForForeground() {

        AmbilWarnaDialog textColorDialog = new AmbilWarnaDialog(this, false, backgroundColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        dialog.getDialog().dismiss();
                        tintColor = color;
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(MainActivity.this, foregroundItem);
                        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                        DrawableCompat.setTint(wrappedDrawable, tintColor);
                        foreground.setImageResource(foregroundItem);
                    }
                });
        textColorDialog.show();

    }

    public void loadDrawables(Class<?> clz, String name) {
        final Field[] fields = clz.getDeclaredFields();
        icons.clear();
        for (Field field : fields) {
            final int drawableId;
            try {
                if (field.getName().contains(name)) {
                    icons.add(field.getInt(clz));
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    @Override
    public void onClickIcon(int pos) {
        foregroundItem = pos;
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(MainActivity.this, pos);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, tintColor);
        foreground.setImageResource(pos);
    }

    @Override
    public void catClickListener(String name) {
        String tmp = name;
        tmp = tmp.toLowerCase();
        tmp = tmp.replace(" ","");
        loadDrawables(R.drawable.class, tmp);
        mainAdapter.notifyDataSetChanged();
    }
}
