package com.shyamu.translocwidget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shyamu.translocwidget.R;


public class AboutActivity extends Activity {
    private TextView tvWebsite;
    private TextView tvApiUrl;
    private TextView tvGithubUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvWebsite = (TextView) findViewById(R.id.tvWebsite);
        tvApiUrl = (TextView) findViewById(R.id.tvApiUrl);
        tvGithubUrl = (TextView) findViewById(R.id.tvGithubURL);

        tvWebsite.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.shyamalpatel.com"))));

        tvApiUrl.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.mashape.com/transloc/"))));

        tvGithubUrl.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.shyamalpatel.com/TransLoc-Android-Widget/"))));

    }
}