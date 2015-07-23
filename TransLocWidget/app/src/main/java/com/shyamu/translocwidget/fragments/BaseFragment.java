package com.shyamu.translocwidget.fragments;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.shyamu.translocwidget.BuildConfig;
import com.shyamu.translocwidget.bl.Utils;


public class BaseFragment extends Fragment {
    static final String TRANSLOC_API_KEY = BuildConfig.TRANSLOC_API_KEY;


    void handleServiceErrors(Utils.TransLocDataType errorFrom, Throwable e, ProgressBar progressBar) {
        Log.e("Fragments", "error in getting list of " + errorFrom, e);
        progressBar.setVisibility(View.INVISIBLE);
        Utils.showAlertDialog(getActivity(), "Error", "No data connection", false);
    }

}
