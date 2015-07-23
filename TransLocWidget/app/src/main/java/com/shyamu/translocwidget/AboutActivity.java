package com.shyamu.translocwidget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shyamu.translocwidget.R;


public class AboutActivity extends Activity {
    private TextView tvTwitter;
    private TextView tvApiUrl;
    private TextView tvGithubUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvTwitter = (TextView) findViewById(R.id.tvTwitter);
        tvApiUrl = (TextView) findViewById(R.id.tvApiUrl);
        tvGithubUrl = (TextView) findViewById(R.id.tvGithubURL);

        tvTwitter.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("twitter://user?screen_name=ShyamuP"));
                startActivity(intent);

            }catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/#!/ShyamuP")));
            }
        });

        tvApiUrl.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://api.transloc.com"))));

        tvGithubUrl.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://shyamu.github.io/TransLoc-Android-Widget/"))));

    }
}