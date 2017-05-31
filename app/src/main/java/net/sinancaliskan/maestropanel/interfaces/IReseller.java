package net.sinancaliskan.maestropanel.interfaces;

import net.sinancaliskan.maestropanel.models.Reply;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SinanCaliskan on 26.03.2017.
 */

public interface IReseller {
    static String TAG="Reseller";
    //this interface showing api access method and path
    //running like ip:9715/Api/v1/Reseller/GetResellers?format=JSON&key=1111111111
    //get method example
    @GET(TAG+"/GetResellers?format=JSON")
    Call<Reply> GetResellers(
            @Query("key") String key
    );
    @POST(TAG+"/Create?format=JSON")
    Call<Reply>Create(
            @Query("key") String key,
            @Query("username") String username,
            @Query("password") String password,
            @Query("planAlias") String planAlias,
            @Query("firstname") String firstName,
            @Query("lastname") String lastName,
            @Query("email") String email,
            @Query("country") String country,
            @Query("organization") String organization,
            @Query("address1") String address1,
            @Query("address2") String address2,
            @Query("city") String city,
            @Query("province") String province,
            @Query("postalcode") String postalCode,
            @Query("phone") String phone,
            @Query("fax") String fax
    );
    //region reseller user
    @POST(TAG+"/AddDomain?format=JSON")
    Call<Reply>AddDomain(
            @Query("key") String key,
            @Query("username") String username,
            @Query("domainName") String domainName,
            @Query("planAlias") String planAlias,
            @Query("domainUsername") String domainUsername,
            @Query("domainPassword") String domainPassword,
            @Query("activedomainuser") String activeDomainUser
    );
    @POST(TAG+"/AddDomain?format=JSON")
    Call<Reply>AddDomain(
            @Query("key") String key,
            @Query("username") String username,
            @Query("domainName") String domainName,
            @Query("planAlias") String planAlias,
            @Query("domainUsername") String domainUsername,
            @Query("domainPassword") String domainPassword,
            @Query("activedomainuser") String activeDomainUser,
            @Query("firstname") String firstName,
            @Query("lastname") String lastName,
            @Query("email") String email,
            @Query("expiration") String expiration,//yyyy-MM-dd
            @Query("ipaddr") String ipaddr//192.168.2.1
    );
    @DELETE(TAG+"/Delete?format=JSON")
    Call<Reply>Delete(
            @Query("key") String key,
            @Query("username") String username
    );
    @POST(TAG+"/Start?format=JSON")
    Call<Reply>Start(
            @Query("key") String key,
            @Query("username") String username
    );
    @POST(TAG+"/Stop?format=JSON")
    Call<Reply>Stop(
            @Query("key") String key,
            @Query("username") String username
    );
    @POST(TAG+"/ChangePassword?format=JSON")
    Call<Reply>ChangePassword(
            @Query("key") String key,
            @Query("username") String username,
            @Query("newpassword") String newpassword
    );
    @GET(TAG+"/GetDomains?format=JSON")
    Call<Reply> GetDomains(
            @Query("key") String key,
            @Query("username") String username
    );
    @GET(TAG+"/GetIPAddrList?format=JSON")
    Call<Reply> GetIPAddrList(
            @Query("key") String key,
            @Query("username") String username
    );
    //endregion
}
