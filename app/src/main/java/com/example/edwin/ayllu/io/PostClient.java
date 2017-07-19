package com.example.edwin.ayllu.io;

import com.example.edwin.ayllu.io.ApiConstants;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostClient {

    @Multipart
    @POST("upload.php")
    Call<String> uploadAttachment (@Part MultipartBody.Part filePart);

    @Multipart
    @POST("upload3.php")
    Call<String> upLoad3(@Part MultipartBody.Part filePart,
                         @Part MultipartBody.Part filePart2,
                         @Part MultipartBody.Part filePart3);

    @Multipart
    @POST("upload2.php")
    Call<String> upLoad2(@Part MultipartBody.Part filePart,
                         @Part MultipartBody.Part filePart2);


    Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiConstants.URL_CAMERA)
            .addConverterFactory(GsonConverterFactory.create()).build();
}
