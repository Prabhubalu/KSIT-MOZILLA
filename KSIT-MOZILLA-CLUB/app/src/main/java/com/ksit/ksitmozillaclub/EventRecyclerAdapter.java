package com.ksit.ksitmozillaclub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.CustomViewHolder> {

    private AdapterCallback mAdapterCallback;

    private List<Event> eventList;
    private Context mContext;

    public EventRecyclerAdapter(Context mContext, List<Event> eventList) {
        this.eventList = eventList;
        this.mContext = mContext;

        try {
            this.mAdapterCallback = (AdapterCallback) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, null);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        final int eventId;
        final String eventName;
        final Event event = eventList.get(position);

        // Downloading image using the picasso library
        Picasso.with(mContext)
                .load(event.getImgSrc())
                .into(holder.ivImage);

        // Setting TextView with Necessary Information
        holder.tvName.setText(Html.fromHtml(event.getName()));
        holder.tvDetails.setText(Html.fromHtml(event.getDetails()));
        holder.tvDateTime.setText(Html.fromHtml(event.getDatetime()));
        holder.tvVenue.setText(Html.fromHtml(event.getVenue()));

        eventId = event.getId();
        eventName = event.getName();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAdapterCallback.onMethodCallback(eventId, eventName);
                } catch (ClassCastException exception) {
                    Log.d("EventRecyclerAdapter", "Click not functioning!");
                }
            }
        });

        holder.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAdapterCallback.onMethodCallback(eventId, eventName);
                } catch (ClassCastException exception) {
                    Log.d("EventRecyclerAdapter", "Click not functioning!");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != eventList ? eventList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relativeLayout;
        protected ImageView ivImage;
        protected TextView tvName, tvDetails, tvVenue, tvDateTime;
        protected Button btnRegister;

        public CustomViewHolder(View itemView) {
            super(itemView);

            this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
            this.ivImage = (ImageView) itemView.findViewById(R.id.image_view);
            this.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            this.tvDetails = (TextView) itemView.findViewById(R.id.tv_details);
            this.tvVenue = (TextView) itemView.findViewById(R.id.tv_venue);
            this.tvDateTime = (TextView) itemView.findViewById(R.id.tv_datetime);

            this.btnRegister = (Button) itemView.findViewById(R.id.btn_register);
        }
    }

    public interface AdapterCallback {
        void onMethodCallback(int eventId, String eventName);
    }
}

