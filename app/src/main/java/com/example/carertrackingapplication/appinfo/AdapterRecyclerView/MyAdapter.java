package com.example.carertrackingapplication.appinfo.AdapterRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carertrackingapplication.R;
import com.example.carertrackingapplication.appinfo.Appointment;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Appointment> list;

    public MyAdapter(Context context, ArrayList<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //return object of View class.
        View v = LayoutInflater.from(context).inflate(R.layout.appointments,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Appointment appointment = list.get(position);
       holder.addressTV.setText(appointment.getAddress());
       holder.dateTV.setText(appointment.getDate());
       holder.timeTV.setText(appointment.getTime());
       holder.durationTV.setText(appointment.getDuration());
       holder.nameTV.setText(appointment.getName());
       holder.notesTV.setText(appointment.getNotes());

    }

    @Override
    public int getItemCount() {
        return list.size(); //return the size of the array.
    }

    //Inner class
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView addressTV, dateTV, timeTV, durationTV, nameTV, notesTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            addressTV = itemView.findViewById(R.id.appointManageViewAddress);
            dateTV = itemView.findViewById(R.id.HistoryViewTimeField);
            timeTV = itemView.findViewById(R.id.appointManageViewTime);
            durationTV = itemView.findViewById(R.id.appointManageViewDuration);
            nameTV = itemView.findViewById(R.id.appointManageViewName);
            notesTV = itemView.findViewById(R.id.appointManageViewNotes);
        }
    }
}
