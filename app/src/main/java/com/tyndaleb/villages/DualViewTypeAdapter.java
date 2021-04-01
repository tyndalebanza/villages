package com.tyndaleb.villages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class DualViewTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        public Editor renderer ;


        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.delete = (TextView) itemView.findViewById(R.id.delete);
            this.edit = (TextView) itemView.findViewById(R.id.edit);
            // this.txtEdit = (TextView) itemView.findViewById(R.id.text_writeup);
            this.username = (TextView) itemView.findViewById(R.id.username);
            this.comments = (ImageView) itemView.findViewById(R.id.comments);
            this.no_of_comments = (TextView) itemView.findViewById(R.id.no_of_comments);
            this.no_of_likes = (TextView) itemView.findViewById(R.id.no_of_likes);
            this.likes = (ImageView)  itemView.findViewById(R.id.likes);
            this.renderer= (Editor)itemView.findViewById(R.id.renderer);
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

    public DualViewTypeAdapter(ArrayList<village_item> data, Context context ) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case village_item.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_text_row, parent, false);
                return new TextTypeViewHolder(view);
            case village_item.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_image_row, parent, false);
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
                    Map<Integer, String> headingTypeface = getHeadingTypeface();
                    Map<Integer, String> contentTypeface = getContentface();
                    ((TextTypeViewHolder) holder).renderer.setHeadingTypeface(headingTypeface);
                    ((TextTypeViewHolder) holder).renderer.setContentTypeface(contentTypeface);
                    ((TextTypeViewHolder) holder).renderer.setDividerLayout(R.layout.tmpl_divider_layout);
                    ((TextTypeViewHolder) holder).renderer.setEditorImageLayout(R.layout.tmpl_image_view);
                    ((TextTypeViewHolder) holder).renderer.setListItemLayout(R.layout.tmpl_list_item);
                    String content= object.write_up;
                    EditorContent Deserialized= ((TextTypeViewHolder) holder).renderer.getContentDeserialized(content);
                    ((TextTypeViewHolder) holder).renderer.render(Deserialized);
                    // ((TextTypeViewHolder) holder).txtEdit.setText(object.write_up);
                    String str = "Submitted by " + object.fullname ;
                    ((TextTypeViewHolder) holder).username.setText(str);
                    ((TextTypeViewHolder) holder).username.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            Intent intent = new Intent(mContext, BioLinkActivity.class);
                            intent.putExtra("EXTRA_USER_ID", String.valueOf(object.user_id));
                            mContext.startActivity(intent);

                        }
                    });
                    ((TextTypeViewHolder) holder).comments.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            Intent intent = new Intent(mContext, ChatBoxActivity.class);
                            intent.putExtra("extra_thread_id", object.village_thread_id);
                            mContext.startActivity(intent);

                        }
                    });
                    ((TextTypeViewHolder) holder).likes.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            if(object.like_id == 0 ){
                                ((TextTypeViewHolder) holder).likes.setImageResource(R.drawable.thumbs_up_blue) ;
                                ((TextTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes + 1));
                                object.like_id = 1 ;
                                updateFav( object.village_thread_id,user_id);
                            }


                        }
                    });
                    if(object.like_id  > 0 ){
                        ((TextTypeViewHolder) holder).likes.setImageResource(R.drawable.thumbs_up_blue) ;
                    }
                    if(object.likes < 1000 ) {
                        ((TextTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes));
                    } else{
                        ((TextTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes/1000) + "K");
                    }
                    if(object.comments < 1000 ) {
                        ((TextTypeViewHolder) holder).no_of_comments.setText(String.valueOf(object.comments));
                    } else{
                        ((TextTypeViewHolder) holder).no_of_comments.setText(String.valueOf(object.comments/1000) + "K");
                    }

                    break;
                case village_item.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).txtCaption.setText(object.caption);
                    imageLoader = CustomVolleyRequest.getInstance(mContext).getImageLoader();
                    imageLoader.get(object.image, ImageLoader.getImageListener(((ImageTypeViewHolder) holder).image, R.drawable.loading, R.drawable.loading));
                    ((ImageTypeViewHolder) holder).image.setImageUrl(object.image, imageLoader);
                    String str01 = "Submitted by " + object.fullname ;
                    ((ImageTypeViewHolder) holder).username.setText(str01);
                    ((ImageTypeViewHolder) holder).username.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            Intent intent = new Intent(mContext, BioLinkActivity.class);
                            intent.putExtra("EXTRA_USER_ID", String.valueOf(object.user_id));
                            mContext.startActivity(intent);

                        }
                    });
                    ((ImageTypeViewHolder) holder).likes.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            if(object.like_id == 0 ){
                                ((ImageTypeViewHolder) holder).likes.setImageResource(R.drawable.thumbs_up_blue) ;
                                ((ImageTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes + 1));
                                object.like_id = 1 ;
                                updateFav( object.village_thread_id,user_id);
                            }


                        }
                    });
                    if(object.like_id  > 0 ){
                        ((ImageTypeViewHolder) holder).likes.setImageResource(R.drawable.thumbs_up_blue) ;
                    }
                    if(object.likes < 1000 ) {
                        ((ImageTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes));
                    } else{
                        ((ImageTypeViewHolder) holder).no_of_likes.setText(String.valueOf(object.likes/1000) + "K");
                    }
                    ((ImageTypeViewHolder) holder).comments.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            /// button click event

                            Intent intent = new Intent(mContext, ChatBoxActivity.class);
                            intent.putExtra("extra_thread_id", object.village_thread_id);
                            mContext.startActivity(intent);

                        }
                    });
                    if(object.comments < 1000 ) {
                        ((ImageTypeViewHolder) holder).no_of_comments.setText(String.valueOf(object.comments));
                    } else{
                        ((ImageTypeViewHolder) holder).no_of_comments.setText(String.valueOf(object.comments/1000) + "K");
                    }

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


    private void updateFav( final int thread_id  , final String user_id ) {

        // Tag used to cancel the request
        String tag_string_req = "req_new_item";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_FAV, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        //Start New Activity
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Network Error Message");
                        builder.setMessage("Network Error , please try again");
                        builder.setPositiveButton("OK", null);
                        //builder.setNegativeButton("Cancel", null);
                        builder.create().show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Network Error Message");
                    builder.setMessage("Network Error , please try again");
                    builder.setPositiveButton("OK", null);
                    //builder.setNegativeButton("Cancel", null);
                    builder.create().show();


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e(TAG, "Login Error: " + error.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Network Error Message");
                builder.setMessage("Network Error , please try again");
                builder.setPositiveButton("OK", null);
                //builder.setNegativeButton("Cancel", null);
                builder.create().show();


                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {


                String str_village_thread_id = String.valueOf(thread_id) ;
                // String str_user_id = String.valueOf(user_id) ;

                Map<String, String> params = new HashMap<String, String>();
                params.put("village_thread_id", str_village_thread_id);
                params.put("user_id", user_id);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public Map<Integer,String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/GreycliffCF-Bold.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/GreycliffCF-Bold.ttf");
        return typefaceMap;
    }

    public Map<Integer,String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL,"fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD,"fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC,"fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }


}
