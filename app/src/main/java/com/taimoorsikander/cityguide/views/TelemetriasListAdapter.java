package com.taimoorsikander.cityguide.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taimoorsikander.cityguide.R;
import com.taimoorsikander.cityguide.models.TelemetriaEntity;

import java.util.List;



public class TelemetriasListAdapter extends RecyclerView.Adapter<TelemetriasListAdapter.TelemetriaViewHolder> {

    class TelemetriaViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        private TelemetriaViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<TelemetriaEntity> itemsList;

    /**
     * Constructor
     *
     * @param context context
     */
    public TelemetriasListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TelemetriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new TelemetriaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TelemetriaViewHolder holder, int position) {
        if (itemsList != null) {
            TelemetriaEntity current = itemsList.get(position);
            holder.textView.setText(current.getTimestamp());
        } else {
            // Covers the case of data not being ready yet.
            holder.textView.setText("No item");
        }
    }

    public void setItems(List<TelemetriaEntity> userList){
        itemsList = userList;
        notifyDataSetChanged();
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return (itemsList == null)
                ? 0
                : itemsList.size();
    }

    public TelemetriaEntity getItemAtPosition (int position) {
        return itemsList.get(position);
    }
}
