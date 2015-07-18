package com.shyamu.translocwidget.fragments;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.shyamu.translocwidget.bl.Utils;

/**
 * Created by Shyamal on 7/18/2015.
 */
public class BaseFragment extends Fragment {

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
