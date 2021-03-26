package com.tyndaleb.villages;

import java.util.ArrayList;

public class user_chat {

    private String fullname , profile_image,chat_text ;
    private  int user_id,song_id,created_at;
    private ArrayList<String> chat_list;

    public user_chat() {
    }

    public user_chat(int user_id, String fullname, String profile_image, int song_id, String chat_text, int created_at) {
        this.user_id = user_id ;
        this.fullname = fullname ;
        this.profile_image = profile_image ;
        this.song_id = song_id ;
        this.chat_text = chat_text ;
        this.created_at = created_at ;
    }
    public String getFullname() {
        return fullname ;
    }
    public String getProfile_image() {
        return profile_image ;
    }
    public String getChat_text() {
        return chat_text ;
    }
    public  int getUser_id(){return  user_id;}
    public  int getSong_id(){return  song_id;}
    public  int getCreated_at(){return  created_at;}

    public ArrayList<String> getChat_list() {
        return chat_list;
    }
}
