package com.example.doubletap;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TopsectionFragment extends Fragment {
    private static EditText txttop;
    private static EditText txtbottom;

    Topseclistener activityCommander;
    public interface Topseclistener{
        public void creatememe(String Top, String Bottom);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    try {
        activityCommander = (Topseclistener)getActivity();
    }catch (ClassCastException e){
        throw new ClassCastException(getActivity().toString());
    }
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.toppartfragment,container,false);
        txttop = (EditText) view.findViewById(R.id.bottomtextinput);
        txtbottom = (EditText) view.findViewById(R.id.Toptextinput);
          Button btn = (Button) view.findViewById(R.id.button);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityCommander.creatememe(txttop.getText().toString(), txtbottom.getText().toString());

                    }
                }
        );

        return view;
    }



}
