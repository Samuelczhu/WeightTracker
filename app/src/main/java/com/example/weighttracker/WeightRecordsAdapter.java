package com.example.weighttracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The WeightRecordsAdapter is the recycler view adapter for the weight records recycler view
 */
public class WeightRecordsAdapter extends RecyclerView.Adapter<WeightRecordsAdapter.WeightRecordsViewHolder> {

    //Declare some local variables
    private ArrayList<WeightRecord> weightRecords;
    private OnItemClickListener onItemClickListener;

    //On item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Function to set the on item click listener
     * @param listener On-item click listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * The WeightRecordsViewHolder is the view holder for the weight records
     */
    public static class WeightRecordsViewHolder extends RecyclerView.ViewHolder {

        //Declare some public fields for the views from the weight record item
        public TextView tvItemWeight;
        public TextView tvItemRecordTime;

        /**
         * Default constructor for the weight record view holder
         * @param itemView View for the weight record item
         * @param listener Listener for the on item click event
         */
        public WeightRecordsViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            //Find the views
            tvItemWeight = itemView.findViewById(R.id.tv_item_weight);
            tvItemRecordTime = itemView.findViewById(R.id.tv_item_recordTime);

            //Set the on click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check for the listener
                    if (listener != null) {
                        //Get the position
                        int position = getAdapterPosition();
                        //Check for position
                        if (position != RecyclerView.NO_POSITION) {
                            //Invoke the listener
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * Default constructor for the WeightRecordsAdapter class
     * @param weightRecords The array list of weight records
     */
    public WeightRecordsAdapter(ArrayList<WeightRecord> weightRecords) {
        //Set the weight records
        this.weightRecords = weightRecords;
    }

    @NonNull
    @Override
    public WeightRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the view for the weight record
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_record_item, parent, false);
        //Create and return the view holder
        return new WeightRecordsViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightRecordsAdapter.WeightRecordsViewHolder holder, int position) {
        //Get the current weight record
        WeightRecord currentRecord = this.weightRecords.get(position);
        //Setup the display for the view holder
        holder.tvItemWeight.setText(String.format("%.2f",currentRecord.getWeight()));
        holder.tvItemRecordTime.setText(currentRecord.getTimestampString());
    }

    @Override
    public int getItemCount() {
        //Return the number of items in the weight records list
        return this.weightRecords.size();
    }
}
