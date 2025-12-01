package com.example.m_hike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.models.Observation;

import java.util.ArrayList;
import java.util.List;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.VH> {
    private List<Observation> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Observation o);
        void onItemLongClick(Observation o);
    }

    public ObservationAdapter(OnItemClickListener l) { listener = l; }

    public void setList(List<Observation> data) { list = data == null ? new ArrayList<>() : data; notifyDataSetChanged(); }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Observation o = list.get(position);
        holder.tvText.setText(o.getObsText());
        holder.tvTime.setText(o.getTimestamp());
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(o); });
        holder.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onItemLongClick(o); return true; });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        VH(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.itemObsText);
            tvTime = itemView.findViewById(R.id.itemObsTime);
        }
    }
}
