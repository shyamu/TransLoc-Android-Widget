package com.shyamu.translocwidget.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.shyamu.translocwidget.MainActivity;
import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;
import com.shyamu.translocwidget.rest.model.TransLocStop;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.MainActivity.*;
import static com.shyamu.translocwidget.bl.Utils.TransLocDataType.STOP;


public class SelectStopFragment extends BaseFragment {
    private final String TAG = this.getTag();
    ListView stopListView;
    ProgressBar progressBar;

    ArrivalTimeWidget atw;

    public SelectStopFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_empty, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stop, container, false);

        Bundle args = getArguments();
        if (args  != null && args.containsKey("atw")) {
            atw = (ArrivalTimeWidget) args.getSerializable("atw");
        } else {
            throw new IllegalStateException("No atw received from SelectRouteFragment");
        }

        setHasOptionsMenu(true);
        stopListView = (ListView) rootView.findViewById(R.id.lvStopList);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        progressBar.setVisibility(View.VISIBLE);
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        STOP);
        client.stops(atw.getAgencyID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::populateStopsListView,
                        e ->  handleServiceErrors(getActivity(), STOP, e, progressBar)
                );
        return rootView;
    }

    private void populateStopsListView(List<TransLocStop> stops) {
        progressBar.setVisibility(View.INVISIBLE);
        if (stops != null && !stops.isEmpty()) {
            ArrayList<TransLocStop> stopList = new ArrayList<>();
            for (TransLocStop stop : stops) {
                if (stop.routes.contains(Integer.parseInt(atw.getRouteID()))) {
                    stopList.add(stop);
                }
                ArrayAdapter<TransLocStop> stopArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stopList);
                stopListView.setAdapter(stopArrayAdapter);

                // Animate
                TranslateAnimation animate = new TranslateAnimation(stopListView.getWidth(),0,0,0);
                animate.setDuration(250);
                animate.setFillAfter(true);
                stopListView.startAnimation(animate);
                stopListView.setVisibility(View.VISIBLE);

                // Set onclicklistener to open select stops fragment
                stopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TransLocStop selectedStop = (TransLocStop) parent.getItemAtPosition(position);
                        atw.setStopID((Integer.toString(selectedStop.stopId)));
                        atw.setStopName(selectedStop.toString());

                        CustomizeColorsFragment customizeColorsFragment = new CustomizeColorsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("atw", atw);
                        customizeColorsFragment.setArguments(bundle);

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                .replace(R.id.widget_container, customizeColorsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        } else {
            Log.e(TAG, "Stops data is null or empty!");
        }
    }
}
