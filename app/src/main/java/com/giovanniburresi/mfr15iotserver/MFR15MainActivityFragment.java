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

    private TextView logTextView;

    private TextView node1TextView;
    private TextView node2TextView;

    public MFR15MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mfr15_main, container, false);

        logTextView   = (TextView) rootView.findViewById(R.id.log);
        node1TextView = (TextView) rootView.findViewById(R.id.node1TextView);
        node2TextView = (TextView) rootView.findViewById(R.id.node2TextView);

        ((MFR15MainActivity)getActivity()).viewBuildComplete();
        return rootView;
    }

    public void appendNotification(String msg){
        logTextView.setText(msg + "\n" + logTextView.getText());
    }

    public void setNode1TextView(String s){
        s = s.replace('|','\n');
        node1TextView.setText("Donatello\n" + s);
    }

    public void setNode2TextView(String s){
        s = s.replace('|','\n');
        node2TextView.setText("Michelangelo\n" + s);
    }

    public void setNode3TextView(String s){}

    public void setNode4TextView(String s){}

    public void setNode5TextView(String s){}

}
