package com.shyamu.translocwidget.fragments;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.shyamu.translocwidget.BuildConfig;
import com.shyamu.translocwidget.bl.Utils;


public class BaseFragment extends Fragment {
    protected static final String TRANSLOC_API_KEY= BuildConfig.TRANSLOC_API_KEY;

   protected void handleServiceErrors(Context context, Utils.TransLocDataType errorFrom, Throwable e, ProgressBar progressBar) {
        Log.e("Fragments", "error in getting list of " + errorFrom, e);
        progressBar.setVisibility(View.INVISIBLE);
        StringBuilder sb = new StringBuilder();
        sb.append("Error in retreiving a list of ");
        String listOf = null;
        switch (errorFrom) {
            case AGENCY:
                listOf = "agencies.";
                break;
            case ROUTE:
                listOf = "routes.";
                break;
            case STOP:
                listOf = "stops.";
                break;
            case ARRIVAL:
                listOf = "arrivals.";
                break;
        }
        sb.append(listOf);
        sb.append(" Please try again later");

        Utils.showAlertDialog(context, "Error", sb.toString());
    }

}
