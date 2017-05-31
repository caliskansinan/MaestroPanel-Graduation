package net.sinancaliskan.maestropanel.interfaces;

import net.sinancaliskan.maestropanel.models.Reply;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SinanCaliskan on 26.03.2017.
 */

public interface IServer {
    static String TAG="Server";
    //this interface showing api access method and path
    //running like ip:9715/Api/v1/Server/GetServerList?format=JSON&key=1111111111
    //get method example
    //region server
    @GET(TAG+"/GetServerList?format=JSON")
    Call<Reply> GetServerList(
            @Query("key") String key
    );
    @GET(TAG+"/GetResources?format=JSON")
    Call<Reply> GetResources(
            @Query("key") String key,
            @Query("servername") String serverName
    );
    @POST(TAG+"/AddServer?format=JSON")
    Call<Reply> AddServer(
            @Query("key") String key,
            @Query("servername") String serverName,
            @Query("host") String host,
            @Query("username") String username,
            @Query("password") String password,
            @Query("agentport") String agentport
    );
    @POST(TAG+"/DeleteServer?format=JSON")
    Call<Reply> DeleteServer(
            @Query("key") String key,
            @Query("servername") String serverName
    );
    //endregion

    //region IP List
    @GET(TAG+"/GetIpAddrList?format=JSON")
    Call<Reply> GetIpAddrList(
            @Query("key") String key
    );
    @GET(TAG+"/GetNicList?format=JSON")
    Call<Reply> GetNicList(
            @Query("key") String key
    );
    @POST(TAG+"/AddIpAddr?format=JSON")
    Call<Reply> AddIpAddr(
            @Query("key") String key,
            @Query("servername") String serverName,
            @Query("nicName") String nicName,
            @Query("ipAddr") String ipAddr,
            @Query("subNet") String subNet,
            @Query("isShared") String isShared,
            @Query("isDedicated") String isDedicated,
            @Query("isExclusive") String isExclusive
    );
    @POST(TAG+"/DeleteIpAddr?format=JSON")
    Call<Reply> DeleteIpAddr(
            @Query("key") String key,
            @Query("servername") String serverName,
            @Query("nicName") String nicName,
            @Query("ipAddr") String ipAddr
    );
    //endregion
}
