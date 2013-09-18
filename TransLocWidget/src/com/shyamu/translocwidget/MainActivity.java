package com.shyamu.translocwidget;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button hideAppIconButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        hideAppIconButton = (Button) findViewById(R.id.bHideAppIcon);

        // On click listener for hide app icon button
        View.OnClickListener setHideAppIconClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager p = getPackageManager();
                p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                Toast.makeText(MainActivity.this,"Icon will be hidden on reboot",Toast.LENGTH_LONG).show();
            }
        };

        hideAppIconButton.setOnClickListener(setHideAppIconClickedListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                // start about activity
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

}
