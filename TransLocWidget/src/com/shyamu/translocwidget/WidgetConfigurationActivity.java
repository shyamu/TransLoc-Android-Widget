package com.shyamu.translocwidget;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
 
public class WidgetConfigurationActivity extends Activity{
    private int mAppWidgetId = 0 ;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_activity); 
 
        // Gettng the reference to the "Set Color" button
        //Button btnSetColor = (Button) findViewById(R.id.btn_set_color);
 
        // Defining a click event listener for the button "Set Color"
        OnClickListener setColorClickedListener  = new OnClickListener() {
 
            @Override
            public void onClick(View v) {
               // colorPicker();
            }
        };
 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
 
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
 
        // Setting the click listener on the "Set Color" button
       // btnSetColor.setOnClickListener(setColorClickedListener);
    }
 /*
    public void colorPicker() {
 
        //      initialColor is the initially-selected color to be shown in the rectangle on the left of the arrow.
        //      for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware of the initial 0xff which is the alpha.
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xff0000ff, new OnAmbilWarnaListener() {
 
            // Executes, when user click Cancel button
            @Override
            public void onCancel(AmbilWarnaDialog dialog){
            }
 
            // Executes, when user click OK button
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Create an Intent to launch WidgetConfigurationActivity screen
                Intent intent = new Intent(getBaseContext(), WidgetConfigurationActivity.class);
 
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
 
                // This is needed to make this intent different from its previous intents
                intent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
 
                // Creating a pending intent, which will be invoked when the user
                // clicks on the widget
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
                                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
 
                // Getting an instance of WidgetManager
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
 
                // Instantiating the class RemoteViews with widget_layout
                RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.widget_layout);
 
                // Setting the background color of the widget
                views.setInt(R.id.widget_aclock, "setBackgroundColor", color);
 
                //  Attach an on-click listener to the clock
                views.setOnClickPendingIntent(R.id.widget_aclock, pendingIntent);
 
                // Tell the AppWidgetManager to perform an update on the app widget
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
 
                // Return RESULT_OK from this activity
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
        dialog.show();
        
    } */
}