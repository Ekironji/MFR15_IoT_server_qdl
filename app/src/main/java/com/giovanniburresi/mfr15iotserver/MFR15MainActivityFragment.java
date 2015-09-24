package com.giovanniburresi.mfr15iotserver;

import android.app.Fragment;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giovanniburresi.mfr15iotserver.cube.MyGLRenderer;
import com.giovanniburresi.mfr15iotserver.cube.MyGLSurfaceView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MFR15MainActivityFragment extends Fragment {

    private TextView logTextView;

    private TextView node1TextView;
    private TextView node2TextView;

    private GLSurfaceView mGLView;

    private float[] AccelerometerValues;
    private float[] MagneticFieldValues;
    private float[] RotationMatrix;

    float[] OrientationValues = new float[3];

    public MFR15MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mfr15_main, container, false);

        logTextView   = (TextView) rootView.findViewById(R.id.log);
        node1TextView = (TextView) rootView.findViewById(R.id.node1TextView);
        node2TextView = (TextView) rootView.findViewById(R.id.node2TextView);

        mGLView = (GLSurfaceView) rootView.findViewById(R.id.glSurfaceView);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(new MyGLRenderer(getActivity().getApplicationContext()));
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mGLView = new MyGLSurfaceView(getActivity());

        AccelerometerValues = new float[3];
        MagneticFieldValues = new float[3];
        RotationMatrix = new float[9];

        ((MFR15MainActivity)getActivity()).viewBuildComplete();
        return mGLView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    public void appendNotification(String msg){
        logTextView.setText(msg + "\n" + logTextView.getText());
    }

    String[] values;
    public void setNode1TextView(String s){
//        s = s.replace('|','\n');
//        node1TextView.setText("Donatello\n" + s);
        values = s.split("\\|");

        if(values.length == 9){
            AccelerometerValues[0] = Float.parseFloat(values[0]);
            AccelerometerValues[1] = Float.parseFloat(values[1]);
            AccelerometerValues[2] = Float.parseFloat(values[2]);
            MagneticFieldValues[0] = Float.parseFloat(values[3]);
            MagneticFieldValues[1] = Float.parseFloat(values[4]);
            MagneticFieldValues[2] = Float.parseFloat(values[5]);

            SensorManager.getRotationMatrix(RotationMatrix, null, AccelerometerValues, MagneticFieldValues);
            SensorManager.getOrientation(RotationMatrix, OrientationValues);

            ((MyGLSurfaceView)mGLView).setOrientation(OrientationValues[0], OrientationValues[1], OrientationValues[2]);
        }
    }

    public void setNode2TextView(String s){
        s = s.replace('|','\n');
        node2TextView.setText("Michelangelo\n" + s);
    }

    public void setNode3TextView(String s){}

    public void setNode4TextView(String s){}

    public void setNode5TextView(String s){}

}
