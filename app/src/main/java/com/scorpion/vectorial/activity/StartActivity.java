package com.scorpion.vectorial.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.scorpion.vectorial.AdUtils.FBInterstitial;
import com.scorpion.vectorial.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView textView =(TextView)findViewById(R.id.privacyPolicy);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://sites.google.com/view/vectorial-wallpaper/home'>Privacy Policy</a>";
        textView.setText(Html.fromHtml(text));

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBInterstitial.getInstance().displayFBInterstitial(StartActivity.this, new FBInterstitial.FbCallback() {
                    public void callbackCall() {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                });
            }
        });

        findViewById(R.id.studio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),StudioActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(StartActivity.this, ExitActivity.class));
        finish();
    }
}
