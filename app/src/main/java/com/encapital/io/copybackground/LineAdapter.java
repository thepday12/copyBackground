package com.encapital.io.copybackground;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;



public class LineAdapter extends RecyclerView.Adapter<LineAdapter.MyViewHolder> {
    private List<String> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNO,tvLine;

        MyViewHolder(View view) {
            super(view);
            tvNO= view.findViewById(R.id.tvNO);
            tvLine = view.findViewById(R.id.tvLine);

        }
    }

    public LineAdapter() {
        this.mDataset = new ArrayList<>();
    }

    public LineAdapter(List<String> datasets) {
        this.mDataset = datasets;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_line, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String lineData = mDataset.get(position);
        holder.tvNO.setText(String.valueOf(position + 1));
        holder.tvLine.setText(lineData);
    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}