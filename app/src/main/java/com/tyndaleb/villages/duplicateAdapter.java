package com.tyndaleb.villages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;



 public class duplicateAdapter extends RecyclerView.Adapter<com.tyndaleb.villages.duplicateAdapter.ViewHolder>  {

     private ImageLoader imageLoader;
     private Context context;
     //private int item_id ;
     private String item_name ;
     private int user_id ;
     private village_dash village_dash ;
     private int video_position ;
     private SQLiteHandler db;
     private SessionManager session;
     private duplicateAdapter.EditListener listener ;

     //List of superHeroes
     List<village_dash> myVillages;

     public duplicateAdapter(List<village_dash> myVillages, Context context, duplicateAdapter.EditListener listener  ) {
         super();
         //Getting all the superheroes
         this.myVillages = myVillages ;
         this.context = context;
         this.listener = listener;
     }

     @Override
     public duplicateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View v = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.dup_adapter_row, parent, false);
         RecyclerView.ViewHolder viewHolder = new duplicateAdapter.ViewHolder(v);
         return (duplicateAdapter.ViewHolder) viewHolder;
     }

     @Override
     public void onBindViewHolder(final duplicateAdapter.ViewHolder holder, int position) {
         final int pos = position;
         village_dash = myVillages.get(pos);
         //video_position = position ;
         holder.village_name.setText(village_dash.getVillage_name());
         holder.country.setText(village_dash.getCountry());
         holder.province.setText(village_dash.getState());

         imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

         imageLoader.get(village_dash.getImage_url(), ImageLoader.getImageListener(holder.image, R.drawable.loading, R.drawable.loading));
         holder.image.setImageUrl(village_dash.getImage_url(), imageLoader);

         holder.edit_pen.setOnClickListener(new View.OnClickListener() {
             //@Override
             public void onClick(View v) {

                 listener.onItemClicked(myVillages.get(pos).getVillage_id());


             }
         });


     }

     @Override
     public int getItemCount() {
         return myVillages.size();
     }

     class ViewHolder extends RecyclerView.ViewHolder
     {


         public TextView village_name ;
         public TextView country ;
         public TextView edit_pen ;
         public TextView province ;
         public NetworkImageView image;

         public ViewHolder(View itemView) {
             super(itemView);


             village_name = (TextView) itemView.findViewById(R.id.VillageName);
             country = (TextView) itemView.findViewById(R.id.Country);
             province = (TextView) itemView.findViewById(R.id.State);
             edit_pen = (TextView) itemView.findViewById(R.id.edit_pen);
             image = (NetworkImageView) itemView.findViewById(R.id.home_image);
         }

     }
     interface EditListener {
         void onItemClicked(int village_id);
     }

 }
