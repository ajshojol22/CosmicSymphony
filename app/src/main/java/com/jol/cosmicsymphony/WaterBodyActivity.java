package com.jol.cosmicsymphony;

import static com.jol.cosmicsymphony.Constants.waterBodiesInfoList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;

import com.jol.cosmicsymphony.adapters.SpeciesAdapter;
import com.jol.cosmicsymphony.data.WaterBodiesInfo;
import com.jol.cosmicsymphony.databinding.ActivityMainBinding;
import com.jol.cosmicsymphony.databinding.ActivityWaterBodyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WaterBodyActivity extends AppCompatActivity {
    ActivityWaterBodyBinding binding;
    Bundle bundle;
    String lakeTitle;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaterBodyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        bundle=getIntent().getExtras();
        lakeTitle=bundle.getString("lakeTitle");
        position=bundle.getInt("position");
        binding.toolbarTitle.setText(lakeTitle);

        WaterBodiesInfo info=waterBodiesInfoList.get(position);
        WaterBodiesInfo.LakeInfo lakeInfo=info.getWater_bodies().get(lakeTitle);
        WaterBodiesInfo.WaterQuality waterQuality= lakeInfo.getWater_quality();
        String temperature=waterQuality.getTemp()+"°C";
        String phLevel=waterQuality.getPh()+"";
        String conductance=waterQuality.getConductance()+"uS/cm @ 25°C";
        String turbidity=waterQuality.getTurbidity()+"FNU";
        String do_level=waterQuality.getDo_value()+"mg/L";

        binding.doValue.setText(do_level);
        binding.tempValue.setText(temperature);
        binding.turbidityValue.setText(turbidity);
        binding.conductanceValue.setText(conductance);
        binding.phValue.setText(phLevel);

        binding.dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.tableLayout.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(binding.cardView, new AutoTransition());
                    binding.tableLayout.setVisibility(View.GONE);
                    binding.dropDown.setImageResource(R.drawable.baseline_expand_more_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(binding.cardView, new AutoTransition());
                    binding.tableLayout.setVisibility(View.VISIBLE);
                    binding.dropDown.setImageResource(R.drawable.baseline_expand_less_24);
                }
            }
        });


        Set<Map.Entry<String, WaterBodiesInfo.EndangeredSpecies>> set = lakeInfo.getEndangered_species().entrySet();

        List<String> speciesList=new ArrayList<>();

        for (Map.Entry<String, WaterBodiesInfo.EndangeredSpecies> me : set) {
            speciesList.add(me.getKey());
            Log.wtf("Key",me.getKey());
        }
        SpeciesAdapter adapter=new SpeciesAdapter(WaterBodyActivity.this,speciesList,lakeInfo);
        binding.viewPager.setAdapter(adapter);


    }
}