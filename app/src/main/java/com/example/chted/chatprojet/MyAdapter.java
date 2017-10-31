package com.example.chted.chatprojet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.NinePatch;
import com.google.gson.JsonObject;


import java.util.List;


/**
 * Created by chted on 25/10/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<JsonObject> dataset;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.mymv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<JsonObject> dataset) {

        this.dataset = dataset ;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message_view, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String login=dataset.get(position).get("login").toString();
        login = login.replace("\"", "");
        String message=dataset.get(position).get("message").toString();
        message = message.replace("\"", "");
        holder.textView.setText(login+" : "+message);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
