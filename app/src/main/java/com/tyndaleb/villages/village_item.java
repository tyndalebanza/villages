package com.tyndaleb.villages;

public class village_item {

    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;


    public int village_id ,user_id ,post_type , village_thread_id , like_id , comments , likes ;
    public String image ,write_up ,category , fullname , profile_photo, caption ;

    public village_item(int village_thread_id ,int village_id,int user_id,int post_type, String image, String write_up , String category, String fullname, String profile_photo, String caption , int like_id,
                        int comments , int likes )
    {
        this.village_id=village_id;
        this.user_id=user_id;
        this.post_type=post_type;
        this.image=image;
        this.write_up=write_up;
        this.category=category;
        this.village_thread_id = village_thread_id ;
        this.fullname = fullname ;
        this.caption = caption ;
        this.profile_photo = profile_photo ;
        this.like_id = like_id;
        this.comments = comments ;
        this.likes = likes ;
    }

    public int getVillage_id() {
        return village_id ;
    }
    public int getUser_id() {
        return user_id ;
    }
    public int getPost_type() {
        return post_type ;
    }
    public String getImage() {
        return image ;
    }
    public String getWrite_up() {
        return write_up ;
    }
    public String getCategory() {
        return category ;
    }
    public String getFullname() {
        return fullname ;
    }
    public String getProfile_photo() {
        return profile_photo ;
    }
    public String getCaption() {
        return caption ;
    }
    public int getLike_id() {
        return like_id ;
    }
    public int getComments() {
        return comments ;
    }
    public int getLikes() {
        return likes ;
    }
}
