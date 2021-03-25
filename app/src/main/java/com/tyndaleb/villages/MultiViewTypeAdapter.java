package com.tyndaleb.villages;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

public class MultiViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<village_item> dataSet;
    Context mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;
    private ImageLoader imageLoader;
    private  EditItemClickedListener listener ;
    private SQLiteHandler db;
    private SessionManager session;
    private String user_id ;

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txtEdit;
        TextView delete;
        TextView edit;
        public TextView username ;
        public ImageView comments ;
        public TextView no_of_comments ;
        public ImageView likes ;
        public TextView no_of_likes ;


        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.delete = (TextView) itemView.findViewById(R.id.delete);
            this.edit = (TextView) itemView.findViewById(R.id.edit);
            this.txtEdit = (TextView) itemView.findViewById(R.id.text_writeup);
            this.username = (TextView) itemView.findViewById(R.id.username);
            this.comments = (ImageView) itemView.findViewById(R.id.comments);
            this.no_of_comments = (TextView) itemView.findViewById(R.id.no_of_comments);
            this.no_of_likes = (TextView) itemView.findViewById(R.id.no_of_likes);
            this.likes = (ImageView)  itemView.findViewById(R.id.likes);
        }

    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {


        TextView txtCaption;
        TextView imgDelete;
        NetworkImageView image;
        public TextView username ;
        public ImageView comments ;
        public TextView no_of_comments ;
        public ImageView likes ;
        public TextView no_of_likes ;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.txtCaption = (TextView) itemView.findViewById(R.id.caption);
            this.username = (TextView) itemView.findViewById(R.id.username);
            this.imgDelete = (TextView) itemView.findViewById(R.id.delete_02);
            this.image = (NetworkImageView) itemView.findViewById(R.id.post_image);
            this.comments = (ImageView) itemView.findViewById(R.id.comments);
            this.no_of_comments = (TextView) itemView.findViewById(R.id.no_of_comments);
            this.no_of_likes = (TextView) itemView.findViewById(R.id.no_of_likes);
            this.likes = (ImageView)  itemView.findViewById(R.id.likes);

        }

    }

    public MultiViewTypeAdapter(ArrayList<village_item> data, Context context,EditItemClickedListener listener ) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
        this.listener = listener ;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case village_item.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.village_text_row, parent, false);
                return new TextTypeViewHolder(view);
            case village_item.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.village_image_row, parent, false);
                return new ImageTypeViewHolder(view);

        }
        return null;


    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).post_type) {
            case 0:
                return village_item.TEXT_TYPE;
            case 1:
                return village_item.IMAGE_TYPE;
            default:
                return -1;
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        // SqLite database handler
        db = new SQLiteHandler(mContext.getApplicationContext());

        // session manager
        session = new SessionManager(mContext.getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("uid");

        final village_item object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.post_type) {
                case village_item.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).txtEdit.setText(object.write_up);
                    String str = "Submitted by " + object.fullname ;
                    ((TextTypeViewHolder) holder).username.setText(str);
                    break;
                case village_item.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).txtCaption.setText(object.caption);
                    imageLoader = CustomVolleyRequest.getInstance(mContext).getImageLoader();
                    imageLoader.get(object.image, ImageLoader.getImageListener(((ImageTypeViewHolder) holder).image, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
                    ((ImageTypeViewHolder) holder).image.setImageUrl(object.image, imageLoader);
                    String str01 = "Submitted by " + object.fullname ;
                    ((ImageTypeViewHolder) holder).username.setText(str01);
                    break;
            }
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    interface EditItemClickedListener {
        void onEditClicked(int village_thread_id, String write_up);
    }

}


