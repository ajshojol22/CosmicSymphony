package com.jol.cosmicsymphony;

import static com.jol.cosmicsymphony.Constants.waterBodiesInfoList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jol.cosmicsymphony.adapters.SpeciesAdapter;
import com.jol.cosmicsymphony.data.WaterBodiesInfo;
import com.jol.cosmicsymphony.databinding.ActivityWaterBodyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class WaterBodyActivity extends AppCompatActivity {
    ActivityWaterBodyBinding binding;
    Bundle bundle;
    String lakeTitle;
    int position;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaterBodyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        bundle = getIntent().getExtras();
        lakeTitle = bundle.getString("lakeTitle");
        position = bundle.getInt("position");
        binding.toolbarTitle.setText(lakeTitle);

        WaterBodiesInfo info = waterBodiesInfoList.get(position);
        WaterBodiesInfo.LakeInfo lakeInfo = info.getWater_bodies().get(lakeTitle);
        WaterBodiesInfo.WaterQuality waterQuality = lakeInfo.getWater_quality();
        String temperature = waterQuality.getTemp() + "°C";
        String phLevel = waterQuality.getPh() + "";
        String conductance = waterQuality.getConductance() + "uS/cm @ 25°C";
        String turbidity = waterQuality.getTurbidity() + "FNU";
        String do_level = waterQuality.getDo_value() + "mg/L";

        binding.doValue.setText(do_level);
        binding.tempValue.setText(temperature);
        binding.turbidityValue.setText(turbidity);
        binding.conductanceValue.setText(conductance);
        binding.phValue.setText(phLevel);

        binding.timelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimelineFragment timelineFragment=new TimelineFragment(lakeTitle);
                timelineFragment.show(getSupportFragmentManager(), "timelineFragment");

            }
        });



        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Hey there, I am at the " + lakeTitle + ".The pH level of this water body";
                if (waterQuality.getPh() >= 7.5 && waterQuality.getPh() <= 8.5) {
                    message = message + " is in the standard level for swimming.";
                } else {
                    message = message + " isn't in the standard level for swimming.";
                }
                message = message + "And the turbidity level";

                if (waterQuality.getTurbidity() < 10) {
                    message = message + " is in the standard range.";
                } else {
                    message = message + " is not in the standard range.";
                }
                message = message + "\nSo you should keep these thing in your mind before coming here.";

                shareToFriends(message);
            }
        });


        if (waterQuality.getPh() >= 7.5 && waterQuality.getPh() <= 8.5) {
            binding.phTick.setImageResource(R.drawable.checked);
            binding.phTickTvJump.setText("The pH level is in the standard range");
        } else {
            binding.phTick.setImageResource(R.drawable.cancel);
            binding.phTickTvJump.setText("The pH level is not in the standard range");
        }

        if (waterQuality.getTurbidity() < 10) {
            binding.turbidityTick.setImageResource(R.drawable.checked);
            binding.turbidityTickTvJump.setText("The turbidity level is in the standard range");
        } else {
            binding.turbidityTick.setImageResource(R.drawable.cancel);
            binding.turbidityTickTvJump.setText("The turbidity level is not in the standard range");

        }


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
        binding.dropDownJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.tableLayoutJump.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(binding.beforeYouJumpCard, new AutoTransition());
                    binding.tableLayoutJump.setVisibility(View.GONE);
                    binding.dropDownJump.setImageResource(R.drawable.baseline_expand_more_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(binding.beforeYouJumpCard, new AutoTransition());
                    binding.tableLayoutJump.setVisibility(View.VISIBLE);
                    binding.dropDownJump.setImageResource(R.drawable.baseline_expand_less_24);
                }
            }
        });


        Set<Map.Entry<String, WaterBodiesInfo.EndangeredSpecies>> set = lakeInfo.getEndangered_species().entrySet();

        List<String> speciesList = new ArrayList<>();

        for (Map.Entry<String, WaterBodiesInfo.EndangeredSpecies> me : set) {
            speciesList.add(me.getKey());
            Log.wtf("Key", me.getKey());
        }
        SpeciesAdapter adapter = new SpeciesAdapter(WaterBodyActivity.this, speciesList, lakeInfo);
        binding.viewPager.setAdapter(adapter);


    }

    void shareToFriends(String shareBody) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String friendlyReminder = "A Friendly Reminder";
        String shareAbout = "Share information about " + lakeTitle;
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareAbout);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, friendlyReminder));
    }

}