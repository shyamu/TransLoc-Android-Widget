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
import com.shyamu.translocwidget.rest.model.TransLocRoute;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.bl.Utils.TransLocDataType.ROUTE;

public class SelectRouteFragment extends BaseFragment {

    private final String TAG = this.getTag();
    ListView routeListView;
    ProgressBar progressBar;

    ArrivalTimeWidget atw;

    public SelectRouteFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_empty, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_route, container, false);

        Bundle args = getArguments();
        if (args  != null && args.containsKey("atw")) {
            atw = (ArrivalTimeWidget) args.getSerializable("atw");
        } else {
            throw new IllegalStateException("No atw received from SelectAgencyFragment");
        }

        setHasOptionsMenu(true);
        routeListView = (ListView) rootView.findViewById(R.id.lvRouteList);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        progressBar.setVisibility(View.VISIBLE);
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        ROUTE);
        client.routes(atw.getAgencyID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::populateRoutesListView,
                        e ->  handleServiceErrors(getActivity(), ROUTE, e, progressBar)
                );
        return rootView;
    }

    private void populateRoutesListView(List<TransLocRoute> routes) {
        progressBar.setVisibility(View.INVISIBLE);
        if (routes != null && !routes.isEmpty()) {
            ArrayAdapter<TransLocRoute> routeArrayAdapter = new ArrayAdapter<TransLocRoute>(getActivity(), android.R.layout.simple_spinner_dropdown_item, routes);
            routeListView.setAdapter(routeArrayAdapter);

            // Animate
            TranslateAnimation animate = new TranslateAnimation(routeListView.getWidth(),0,0,0);
            animate.setDuration(250);
            animate.setFillAfter(true);
            routeListView.startAnimation(animate);
            routeListView.setVisibility(View.VISIBLE);

            // Set onclicklistener to open select stops fragment
            routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TransLocRoute selectedRoute = (TransLocRoute) parent.getItemAtPosition(position);
                    atw.setRouteID(Integer.toString(selectedRoute.routeID));
                    atw.setRouteName(selectedRoute.toString());

                    SelectStopFragment selectStopFragment = new SelectStopFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("atw", atw);
                    selectStopFragment.setArguments(bundle);

                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .replace(R.id.widget_container, selectStopFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        } else {
            Log.e(TAG, "Routes data was null or empty!");
        }
    }
}
