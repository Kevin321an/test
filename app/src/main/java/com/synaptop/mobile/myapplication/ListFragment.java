package com.synaptop.mobile.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synaptop.mobile.myapplication.data.Places;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass to display the list layout
 */
public class ListFragment extends Fragment {

    public static final String LOG_TAG = ListFragment.class.getSimpleName();
    private RecyclerListAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_list, container, false);

        //parse the json file into the List
        ArrayList<Places> x = Utility.loadJSONFromAsset(this.getActivity());

        mRecyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //wrap the json into the RecyclerView and pass the layout type
        mRecyclerAdapter = new RecyclerListAdapter(this.getContext(),RecyclerListAdapter.PAGE_TYPE_LIST);
        mRecyclerAdapter.swapCursor(x);
        //shoot the json file on the screen
        mRecyclerView.setAdapter(mRecyclerAdapter);
        return root;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // hold for transition here just in-case the activity
        // needs to be re-created. In a standard return transition,
        // this doesn't actually make a difference.
        getActivity().supportPostponeEnterTransition();
        super.onActivityCreated(savedInstanceState);
    }










}
