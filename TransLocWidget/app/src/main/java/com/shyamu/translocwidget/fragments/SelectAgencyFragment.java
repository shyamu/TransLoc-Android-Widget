package com.shyamu.translocwidget.fragments;

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

import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;
import com.shyamu.translocwidget.rest.model.TransLocAgency;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.bl.Utils.TransLocDataType.AGENCY;

public class SelectAgencyFragment extends BaseFragment {

    private final String TAG = this.getTag();
    private ListView agencyListView;
    private ProgressBar progressBar;
    private ArrivalTimeWidget atw;

    private Subscription agenciesSub;

    public SelectAgencyFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_empty, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_agency, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Select an Agency");
        agencyListView = (ListView) rootView.findViewById(R.id.lvAgencyList);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        agencyListView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        TRANSLOC_API_KEY,
                        null,
                        AGENCY);
        agenciesSub = client.agencies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::populateAgencyListView,
                        e -> handleServiceErrors(AGENCY, e, progressBar)
                );

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(agenciesSub != null) agenciesSub.unsubscribe();
    }

    private void populateAgencyListView(List<TransLocAgency> agencies) {
        if (agencies != null && !agencies.isEmpty()) {
            ArrayAdapter<TransLocAgency> agencyArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, agencies);
            agencyListView.setAdapter(agencyArrayAdapter);

            // Animate
            TranslateAnimation animate = new TranslateAnimation(agencyListView.getWidth(),0,0,0);
            animate.setDuration(250);
            animate.setFillAfter(true);
            agencyListView.startAnimation(animate);
            agencyListView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            // Set onclicklistener to open select routes fragment
            agencyListView.setOnItemClickListener((parent, view, position, id) -> {
                TransLocAgency selectedAgency = (TransLocAgency) parent.getItemAtPosition(position);
                atw = new ArrivalTimeWidget();
                atw.setAgencyID(Integer.toString(selectedAgency.agencyId));
                atw.setAgencyLongName(selectedAgency.longName);

                SelectRouteFragment selectRouteFragment = new SelectRouteFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("atw", atw);
                selectRouteFragment.setArguments(bundle);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.widget_container, selectRouteFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            Log.e(TAG, "Agencies data was null or empty!");
            Utils.showAlertDialog(getActivity(), "No agencies available", "Please try again later.", true);
        }
    }
}
