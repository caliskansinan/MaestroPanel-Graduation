package net.sinancaliskan.maestropanel.interfaces;

import net.sinancaliskan.maestropanel.models.Reply;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by SinanCaliskan on 11.03.2017.
 */

public interface IUser {
    static String TAG="User";
    //this interface showing api access method and path
    //running like ip:9715/Api/v1/User/Whoami?format=JSON&key=1111111111
    //get method example
    @GET(TAG+"/Whoami?format=JSON")
    Call<Reply> Whoami(
            @Query("key") String key
    );
}