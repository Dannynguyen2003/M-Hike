package com.example.m_hike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.models.Hike;

import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.VH> {
    private List<Hike> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Hike hike);
        void onItemLongClick(Hike hike);
    }

    public HikeAdapter(OnItemClickListener l) { listener = l; }

    public void setList(List<Hike> data) { list = data == null ? new ArrayList<>() : data; notifyDataSetChanged(); }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hike, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Hike h = list.get(position);
        holder.tvName.setText(h.getName());
        holder.tvMeta.setText(h.getLocation() + " • " + h.getDate());
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(h); });
        holder.itemView.setOnLongClickListener(v -> { if (listener != null) listener.onItemLongClick(h); return true; });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta;
        VH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.itemHikeName);
            tvMeta = itemView.findViewById(R.id.itemHikeMeta);
        }
    }
}