package com.ozan_kalan.popular_movies_stage1.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ozan.kalan on 4/28/17.
 */

public class MovieList {

    @SerializedName("page")
    @Expose
    public Integer page;

    @SerializedName("results")
    @Expose
    public List<MovieResult> movieResults = null;

}