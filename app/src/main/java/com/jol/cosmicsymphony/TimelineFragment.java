package com.jol.cosmicsymphony;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jol.cosmicsymphony.adapters.TimeLineAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineFragment extends BottomSheetDialogFragment {
    public TimelineFragment(String lakeTitle) {
        this.lakeTitle = lakeTitle;
        Log.wtf("lake", lakeTitle);
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView eventRV;
    List<String> stringList = new ArrayList<>();
    String lakeTitle;
    TextView timeLineOfTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        eventRV = view.findViewById(R.id.timelineRV);
        timeLineOfTv = view.findViewById(R.id.timeLineOfTv);

        timeLineOfTv.setText("Timeline of " + lakeTitle);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("flow");

        databaseReference.child(lakeTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.wtf("snap", snapshot.toString());
                String timeLineText = snapshot.getValue(String.class);
                String[] eventArray = timeLineText.split("\\.");
                ArrayList<String> eventList = new ArrayList<>(Arrays.asList(eventArray));
                eventRV.setAdapter(new TimeLineAdapter(eventList));
                eventRV.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}
