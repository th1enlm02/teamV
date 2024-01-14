package com.example.teamv.prediction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;

import java.util.ArrayList;

public class Adapter_Predict_h extends RecyclerView.Adapter<Adapter_Predict_h.ViewHolder>{
    private ArrayList<Predict_class> list;
    private Context context;

    public Adapter_Predict_h(ArrayList<Predict_class> data) {
        list = data;
    }
    @NonNull
    @Override
    public Adapter_Predict_h.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predict, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Predict_h.ViewHolder holder, int position) {
        holder.Textview_age.setText(list.get(position).getAge());
        holder.Textview_bmi.setText(list.get(position).getBmi());
        holder.Textview_time.setText(list.get(position).getTimework());
        holder.Textview_env.setText(list.get(position).getEnv());
        holder.Textview_path.setText(list.get(position).getPath());
        holder.Textview_heal.setText(list.get(position).getHeal());
        holder.Textview_sex.setText(list.get(position).getSex());
        holder.Textview_nature.setText(list.get(position).getNature());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Textview_age;
        public TextView Textview_bmi;
        public TextView Textview_time;
        public TextView Textview_env;
        public TextView Textview_path;
        public TextView Textview_heal;
        public TextView Textview_sex;
        public TextView Textview_nature;

        public ViewHolder(View itemView) {
            super(itemView);
            Textview_age = itemView.findViewById(R.id.view_age);
            Textview_bmi = itemView.findViewById(R.id.view_bmi);
            Textview_env = itemView.findViewById(R.id.view_env);
            Textview_path = itemView.findViewById(R.id.view_path);
            Textview_time = itemView.findViewById(R.id.view_time);
            Textview_heal = itemView.findViewById(R.id.view_hc);
            Textview_sex = itemView.findViewById(R.id.view_sex);
            Textview_nature = itemView.findViewById(R.id.view_work);
        }
    }
}
