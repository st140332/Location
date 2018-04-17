package com.vlad.location;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vlad on 12.04.2018.
 */

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.ViewHolder> {
    List<Light> lights;
    private Context context;

    public LightAdapter(List<Light> lights) {
        this.lights = lights;
    }

    @Override
    public LightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LightAdapter.ViewHolder holder, final int position) {
        holder.lat.setText("Latitude: "+Double.toString(lights.get(position).getLatitude()));
        holder.lon.setText("Longitude: "+Double.toString(lights.get(position).getLongitude()));
        holder.light.setText("lx: "+Double.toString(lights.get(position).getLight()));
        holder.date.setText(lights.get(position).getCreatedAt());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lights.get(position).getLatitude(), lights.get(position).getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String city = addresses.get(0).getAddressLine(0);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + lights.get(position).getLatitude()  + ">,<" + lights.get(position).getLongitude() + ">?q=<" + lights.get(position).getLatitude()  + ">,<" + lights.get(position).getLongitude() + ">("+city+")"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lights.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lat;
        public TextView lon;
        public TextView light;
        public TextView date;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            lat = (TextView) itemView.findViewById(R.id.lat);
            lon = (TextView) itemView.findViewById(R.id.lon);
            light = (TextView) itemView.findViewById(R.id.light);
            date = (TextView) itemView.findViewById(R.id.date);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
        }
    }
}
