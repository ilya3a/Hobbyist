package com.yoyo.hobbyist.SignFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yoyo.hobbyist.R;

public class SignButtonsFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private MaterialButton mSignInBtn;
    private MaterialButton mSignUpBtn;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void SignButtonsOnClick(int btnId);
    }
    public SignButtonsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_sign_buttons, container, false);
         mSignInBtn=rootView.findViewById(R.id.sign_in_btn);
         mSignUpBtn=rootView.findViewById(R.id.sign_up_btn);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SignButtonsOnClick(v.getId());
            }
        };
        mSignUpBtn.setOnClickListener(onClickListener);
        mSignInBtn.setOnClickListener(onClickListener);
        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
