package com.example.edwin.ayllu;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by steven on 6/02/17.
 */

public interface PostClient {

    String BASE_URL1="http://138.68.40.165/camara/";

    @Multipart
    @POST("upload.php")
    Call<String> uploadAttachment (@Part MultipartBody.Part filePart);

    public static final Retrofit retrofit = new Retrofit.Builder().baseUrl(PostClient.BASE_URL1)
            .addConverterFactory(GsonConverterFactory.create()).build();
}