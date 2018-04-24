package com.trade.leather.ltassignment;

import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mipstech i5 2 on 20-Mar-18.
 */

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> namelist;
    private ArrayList<String> desclist;
    private ArrayList<String> imagelist;

    public DataAdapter(ArrayList namelist, ArrayList desclist, ArrayList<String> imagelist) {
        this.namelist = namelist;
        this.desclist = desclist;
        this.imagelist = imagelist;

    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleresult, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.text1.setText(namelist.get(i));
        viewHolder.text2.setText(desclist.get(i));
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(imagelist.get(i));
            viewHolder.image_view.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return namelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text1,text2;
        ImageView image_view;

        public ViewHolder(View view) {
            super(view);

            text1 = (TextView)view.findViewById(R.id.textView1);
            text2 = (TextView)view.findViewById(R.id.textView2);
            image_view = (ImageView) view.findViewById(R.id.image);
        }
    }

}