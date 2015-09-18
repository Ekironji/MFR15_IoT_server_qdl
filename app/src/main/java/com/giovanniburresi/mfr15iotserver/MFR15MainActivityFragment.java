package com.giovanniburresi.mfr15iotserver;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MFR15MainActivityFragment extends Fragment {

    TextView logTextView;

    public MFR15MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mfr15_main, container, false);

        logTextView = (TextView) getActivity().findViewById(R.id.log);

        return rootView;
    }

    public void appendNotification(String msg){
        logTextView.setText(logTextView.getText() + "\n" + msg);
    }
}
