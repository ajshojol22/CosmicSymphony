package com.jol.cosmicsymphony.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderAdapter;
import com.google.android.material.button.MaterialButton;
import com.jol.cosmicsymphony.R;
import com.jol.cosmicsymphony.data.WaterBodiesInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SpeciesAdapter extends CardSliderAdapter<SpeciesAdapter.SpeciesAdapterVH> {

    List<String> speciesList = new ArrayList<>();
    WaterBodiesInfo.LakeInfo lakeInfo;
    Context context;

    public SpeciesAdapter(Context context,List<String> speciesList, WaterBodiesInfo.LakeInfo lakeInfo) {
        this.speciesList = speciesList;
        this.lakeInfo = lakeInfo;
        this.context = context;
    }

    @NonNull
    @Override
    public SpeciesAdapter.SpeciesAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new SpeciesAdapterVH(inflate);
    }



    @Override
    public int getItemCount() {
        return speciesList.size();
    }

    @Override
    public void bindVH(@NonNull SpeciesAdapterVH holder, int i) {
        String speciesName= speciesList.get(i);
        WaterBodiesInfo.EndangeredSpecies species = lakeInfo.getEndangered_species().get(speciesName);

        String reason=species.getReason();
        String imageUrl= species.getImageUrl();
        String moreInfo=species.getMore_info();

        String[] reasons = reason.split(",\\s*");

        // Create a StringBuilder to build the formatted output string
        StringBuilder formattedString = new StringBuilder();

        // Iterate through the array and format each disease
        for (int j = 0; j < reasons.length; j++) {
            formattedString.append((j + 1) + ". " + capitalizeFirstLetter(reasons[j]) + "\n");
        }

        holder.speciesDescription.setText(formattedString.toString());
        holder.speciesName.setText(speciesName);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.nasalogo)
                .fit()//.resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                .into(holder.speciesImage);
        holder.learnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(moreInfo);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);

            }
        });

    }
    private String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }


    static class SpeciesAdapterVH extends RecyclerView.ViewHolder {
        TextView speciesName,speciesDescription;
        ImageView speciesImage;
        MaterialButton learnMore;
        public SpeciesAdapterVH(@NonNull View itemView) {
            super(itemView);
            speciesName=itemView.findViewById(R.id.speciesName);
            speciesDescription=itemView.findViewById(R.id.speciesDescription);
            speciesImage=itemView.findViewById(R.id.speciesImage);
            learnMore=itemView.findViewById(R.id.learnMore);

        }
    }

}
