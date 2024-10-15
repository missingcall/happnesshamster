package com.kissspace.room.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;

public class NetEaseMusicResponse implements Serializable {
        public int code;
        public Result result;

    public static class Result implements Serializable
    {
        public ArrayList<Song> songs;
    }


    public static class Song implements Serializable{
        public long id;
        public String name;
        public ArrayList<Artist> ar;
    }

    public static class Artist implements Serializable{
        public String name;
    }
}