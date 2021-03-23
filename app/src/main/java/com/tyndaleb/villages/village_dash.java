package com.tyndaleb.villages;

public class village_dash {

    public String village_name , country , state , image_url ;
    public int village_id ;
    public village_dash( String village_name , String country , String state , int village_id , String imageurl ) {
        this.village_name = village_name ;
        this.country = country ;
        this.state = state ;
        this.village_id = village_id ;
        this.image_url = imageurl ;
    }
    public int getVillage_id() {
        return village_id ;
    }
    public String getVillage_name() {
        return village_name ;
    }
    public String getCountry() {
        return country ;
    }
    public String getState() {
        return state ;
    }
    public String getImage_url(){
        return image_url;
    }
}
