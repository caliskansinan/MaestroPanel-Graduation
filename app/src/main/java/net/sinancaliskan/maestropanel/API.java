package net.sinancaliskan.maestropanel;

import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by SinanCaliskan on 12.03.2017.
 */

public class API {
                                                                                                  //create for instance retrofit component
    private static Retrofit retrofit=null;
    public static Retrofit getClient(){
        if (retrofit==null){
            String address= SharedInformation.getData("serverIP");                                //getting user defined server ip or address
            OkHttpClient okHttpClient = new OkHttpClient.Builder()                                //creating okhttpclient
                    .readTimeout(60, TimeUnit.SECONDS)                                            //set maximum readtimeout
                    .connectTimeout(60, TimeUnit.SECONDS)                                         //set maximum connectiontimeout
                    .build();
            retrofit=new Retrofit.Builder()                                                       //create retrofit component
                    .baseUrl("http://"+ address +":9715/Api/v1/")                                 //set api service
                    .addConverterFactory(JacksonConverterFactory.create())                        //set json converter factory
                    .client(okHttpClient)                                                         //attach okhttpclient
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getClient(String address){
        if (retrofit==null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit=new Retrofit.Builder()
                    .baseUrl("http://"+ address +":9715/Api/v1/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
