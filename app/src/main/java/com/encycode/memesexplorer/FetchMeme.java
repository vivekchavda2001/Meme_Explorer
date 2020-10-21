package com.encycode.memesexplorer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class FetchMeme {
    private static final String URL  = "https://meme-api.herokuapp.com/gimme/";
    public static Memeservice memeService = null;
    public static Memeservice getMemeService(){
            if(memeService == null){
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                memeService =retrofit.create(Memeservice.class);
            }
            return memeService;
    }
    public interface Memeservice{
        @GET("dankmemes/30")
        Call<MemeList> getMemeList();
    }

}
