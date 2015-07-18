package com.shyamu.translocwidget.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shyamu.translocwidget.MainActivity;
import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;
import com.shyamu.translocwidget.listview.ListViewAdapter;

import java.io.IOException;
import java.util.ArrayList;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * A fragment representing a list of Items.
 * <p>
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class WidgetListFragment extends ListFragment {

    private static final String TAG = "WidgetListFragment";
    private OnFragmentInteractionListener mListener;

    private FloatingActionButton addNewWidgetButton;
    private TourGuide tourGuide;


    public static WidgetListFragment newInstance() {
        WidgetListFragment fragment = new WidgetListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public WidgetListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_widget_list, container, false);
        addNewWidgetButton = (FloatingActionButton) rootView.findViewById(R.id.fabAddNewWidget);
        ListViewAdapter widgetListViewAdapter = new ListViewAdapter(getActivity());
        ArrayList<ArrivalTimeWidget> listViewArray = null;
        try {
            listViewArray = Utils.getArrivalTimeWidgetsFromStorage(getActivity());
        } catch (IOException e) {
            try {
                Utils.writeArrivalTimeWidgetsToStorage(getActivity(), new ArrayList<>());
            } catch (IOException e1) {
                Log.e(TAG, "Error in writing empty widget list", e1);
            }
            Log.e(TAG, "Error in getting previous widget list", e);
        }
        if (listViewArray != null) {
            if (listViewArray.isEmpty()) {
                tourGuide = TourGuide.init(getActivity()).with(TourGuide.Technique.Click)
                        .setPointer(new Pointer())
                        .setToolTip(new ToolTip()
                                .setTitle("No saved widgets")
                                .setDescription("Tap the button to add a new widget!")
                                .setGravity(Gravity.TOP | Gravity.LEFT))
                        .playOn(addNewWidgetButton);

            }
            widgetListViewAdapter.setWidgetList(listViewArray);
        }
        setListAdapter(widgetListViewAdapter);

        addNewWidgetButton.setOnClickListener(view -> {
            if(tourGuide != null ) tourGuide.cleanUp();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.widget_container, new MainActivity.AddAgencyFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            ArrivalTimeWidget widget = (ArrivalTimeWidget) l.getItemAtPosition(position);
            if (widget == null) throw new IllegalStateException();
            else {
                Log.d(TAG, widget.toString());
                mListener.onFragmentInteraction(widget);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(ArrivalTimeWidget widget);
    }

}
