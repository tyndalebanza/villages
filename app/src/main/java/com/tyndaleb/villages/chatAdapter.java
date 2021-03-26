package com.tyndaleb.villages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.exoplayer2.Player;

import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder>  {
    private ImageLoader imageLoader;
    private Context context;
    //private int item_id ;
    private String item_name ;
    private int user_id ;
    private user_chat user_chat ;
    private int video_position ;
    private SQLiteHandler db;
    private SessionManager session;

    //List of superHeroes
    List<user_chat> myChats;

    public chatAdapter(List<user_chat> myChats, Context context  ) {
        super();
        //Getting all the superheroes
        this.myChats = myChats ;
        this.context = context;
    }

    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_row, parent, false);
        RecyclerView.ViewHolder viewHolder = new chatAdapter.ViewHolder(v);
        return (chatAdapter.ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final int pos = position;
        user_chat = myChats.get(pos);
        //video_position = position ;


        db = new SQLiteHandler(context.getApplicationContext());

        // session manager
        session = new SessionManager(context.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        final String userid = user.get("uid");

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(user_chat.getProfile_image(), ImageLoader.getImageListener(holder.profile_photo, R.mipmap.ic_launcher, R.mipmap.ic_launcher));
        holder.profile_photo.setImageUrl(user_chat.getProfile_image(), imageLoader);

        holder.username.setText(user_chat.getFullname());
        holder.chat_entry.setText(user_chat.getChat_text());

        if(user_chat.getCreated_at() < 24 ) {
            holder.chat_date.setText(user_chat.getCreated_at() + "h");
        } else {
            holder.chat_date.setText(user_chat.getCreated_at()/24 + "d");
        }

    }

    @Override
    public int getItemCount() {
        return myChats.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements Player.EventListener {

        public CircleImageView profile_photo;
        public TextView username ;
        public TextView chat_entry ;
        public TextView chat_date ;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_photo = (CircleImageView)itemView.findViewById(R.id.profile_photo);
            username = (TextView) itemView.findViewById(R.id.username01);
            chat_entry = (TextView) itemView.findViewById(R.id.chat_entry);
            chat_date = (TextView) itemView.findViewById(R.id.chat_date);
        }

    }


}

