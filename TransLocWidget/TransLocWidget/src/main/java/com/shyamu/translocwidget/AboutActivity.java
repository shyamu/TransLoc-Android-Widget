package com.shyamu.translocwidget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class AboutActivity extends Activity {
    TextView tvTwitter, tvApiUrl, tvGithubUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvTwitter = (TextView) findViewById(R.id.tvTwitter);
        tvApiUrl = (TextView) findViewById(R.id.tvApiUrl);
        tvGithubUrl = (TextView) findViewById(R.id.tvGithubURL);

        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=ShyamuP"));
                    startActivity(intent);

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/ShyamuP")));
                }
            }
        });

        tvApiUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://api.transloc.com")));

            }
        });

        tvGithubUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://shyamu.github.io/TransLoc-Android-Widget/")));
            }
        });

    }
}