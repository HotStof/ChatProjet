package com.example.chted.chatprojet;

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.JsonObject;


import java.util.List;


/**
 * Created by chted on 25/10/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<JsonObject> data;

    // Provide a reference to the views for each data item
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.mymv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(List<JsonObject> data) {

        this.data = data ;
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
        String login=data.get(position).get("login").toString();
        login = login.replace("\"", "");
        String message=data.get(position).get("message").toString();
        message = message.replace("\"", "");
        holder.textView.setText(String.format("%s : %s", login, message));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(JsonObject message){
        data.add(message);
        notifyDataSetChanged();
    }

}
