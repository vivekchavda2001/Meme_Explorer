
package com.encycode.memesexplorer;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemeList {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("memes")
    @Expose
    private List<Meme> memes = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Meme> getMemes() {
        return memes;
    }

    public void setMemes(List<Meme> memes) {
        this.memes = memes;
    }

}
