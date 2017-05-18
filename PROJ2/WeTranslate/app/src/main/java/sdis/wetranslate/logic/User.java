package sdis.wetranslate.logic;

import android.content.SharedPreferences;

public class User {

    private static User instance=null;

    private String username=null;
    private int currentRequestWatching=-1;

    protected User(String username){
        this.username=username;
    }

    public static User getInstance(){
        return instance;
    }

    public static void initSession(String username){
        instance=new User(username);
    }

    public String getUsername(){
        return username;
    }

    public void setCurrentRequestWatching(int requestWatching){
        this.currentRequestWatching=requestWatching;
    }

    public int getCurrentRequestWatching(){
        return currentRequestWatching;
    }
}