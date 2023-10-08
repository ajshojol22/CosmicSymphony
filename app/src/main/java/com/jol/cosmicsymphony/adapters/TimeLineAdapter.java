package com.jol.cosmicsymphony.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;
import com.jol.cosmicsymphony.R;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {

    List<String> eventList;

    public TimeLineAdapter(List<String> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        return new TimeLineViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, int position) {
        holder.textView.setText(eventList.get(position));
        if(position==0)
        holder.mTimelineView.initLine(1);
        else if (position== eventList.size()-1)
            holder.mTimelineView.initLine(2);
        else
            holder.mTimelineView.initLine(0);

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TimelineView mTimelineView;
        TextView textView;

        public TimeLineViewHolder(View itemView) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeline);
            textView = itemView.findViewById(R.id.eventTv);


        }
    }
}
