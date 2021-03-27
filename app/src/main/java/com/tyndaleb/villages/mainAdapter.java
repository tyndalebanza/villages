package com.tyndaleb.villages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class mainAdapter extends RecyclerView.Adapter<com.tyndaleb.villages.mainAdapter.ViewHolder>  {

    private ImageLoader imageLoader;
    private Context context;
    //private int item_id ;
    private String item_name ;
    private int user_id ;
    private village_dash village_dash ;
    private int video_position ;
    private SQLiteHandler db;
    private SessionManager session;
    private mainAdapter.EditListener listener ;

    //List of superHeroes
    List<village_dash> myVillages;

    public mainAdapter(List<village_dash> myVillages, Context context  ) {
        super();
        //Getting all the superheroes
        this.myVillages = myVillages ;
        this.context = context;

    }

    @Override
    public mainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_adapter_row, parent, false);
        RecyclerView.ViewHolder viewHolder = new mainAdapter.ViewHolder(v);
        return (mainAdapter.ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(final mainAdapter.ViewHolder holder, int position) {
        final int pos = position;
        village_dash = myVillages.get(pos);
        //video_position = position ;
        holder.village_name.setText(village_dash.getVillage_name());
        holder.country.setText(village_dash.getCountry());
        holder.province.setText(village_dash.getState());

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        imageLoader.get(village_dash.getImage_url(), ImageLoader.getImageListener(holder.image, R.drawable.loading, R.drawable.loading));
        holder.image.setImageUrl(village_dash.getImage_url(), imageLoader);


    }

    public int getVillageId( int position){
        village_dash = myVillages.get(position);
        return  village_dash.getVillage_id();
    }

    @Override
    public int getItemCount() {
        return myVillages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {


        public TextView village_name ;
        public TextView country ;
        // public TextView edit_pen ;
        public TextView province ;
        public NetworkImageView image;

        public ViewHolder(View itemView) {
            super(itemView);


            village_name = (TextView) itemView.findViewById(R.id.VillageName);
            country = (TextView) itemView.findViewById(R.id.Country);
            province = (TextView) itemView.findViewById(R.id.State);
            // edit_pen = (TextView) itemView.findViewById(R.id.edit_pen);
            image = (NetworkImageView) itemView.findViewById(R.id.home_image);
        }

    }
    interface EditListener {
        void onItemClicked(int village_id);
    }

}

