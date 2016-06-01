package com.hotelstonightdatepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by vika on 30.05.16.
 */
public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        final Button button = (Button) view.findViewById(R.id.datePickerBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   new CustomDatePickerFragment().show(getFragmentManager(), CustomDatePickerFragment.class.getName());

                Intent intent = new Intent(getActivity(), CustomDatePickerActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), button, "datePicker");
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//                getActivity().startActivity(intent);

            }
        });
        return view;
    }
}
