package com.ozan_kalan.popular_movies_stage1.Models;

/**
 * Created by ozan.kalan on 6/12/17.
 */

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Videos implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideoResults> results = null;
    private final static long serialVersionUID = 2182491159912146126L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<VideoResults> getResults() {
        return results;
    }

    public void setResults(List<VideoResults> results) {
        this.results = results;
    }

}


