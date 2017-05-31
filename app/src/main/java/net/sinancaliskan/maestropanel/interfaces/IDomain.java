package net.sinancaliskan.maestropanel.interfaces;

import net.sinancaliskan.maestropanel.models.Reply;

import java.io.CharArrayReader;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IDomain {
    static String TAG="Domain";
    //this interface showing api access method and path
    //running like ip:9715/Api/v1/Domain/GetList?format=JSON&key=1111111111
    //get method example
    @GET(TAG+"/GetList?format=JSON")
    Call<Reply> GetList(
            @Query("key") String key
    );
    //region web
    //post method example
    @POST(TAG+"/Create?format=JSON")
    Call<Reply> Create(
            @Query("key") String key,
            @Query("name")String name,
            @Query("planAlias") String planAlias,
            @Query("username") String username,
            @Query("password") String password,
            @Query("activedomainuser") String activeDomainUser
    );
    @POST(TAG+"/Create?format=JSON")
    Call<Reply> Create(
            @Query("key") String key,
            @Query("name")String name,
            @Query("planAlias") String planAlias,
            @Query("username") String username,
            @Query("password") String password,
            @Query("activedomainuser") String activeDomainUser,
            @Query("firstname") String firstName,
            @Query("lastname") String lastName,
            @Query("email") String email,
            @Query("expiration") String expiration,//yyyy-MM-dd
            @Query("ipaddr") String ipaddr//192.168.2.1
    );
    @GET(TAG+"/GetListItem?format=JSON")
    Call<Reply> GetListItem(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/Password?format=JSON")
    Call<Reply>ChangePassword(
            @Query("key") String key,
            @Query("name")String name,
            @Query("newpassword") String newpassword
    );
    @POST(TAG+"/Start?format=JSON")
    Call<Reply> Start(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/Stop?format=JSON")
    Call<Reply> Stop(
            @Query("key") String key,
            @Query("name")String name
    );
    @DELETE(TAG+"/Delete?format=json")
    Call<Reply>Delete(
            @Query("key") String key,
            @Query("name")String name
    );
    //endregion

    //region subdomain
    @GET(TAG+"/GetSubDomains?format=JSON")
    Call<Reply>GetSubDomains(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddSubDomain?format=JSON")
    Call<Reply>AddSubDomain(
            @Query("key") String key,
            @Query("name")String name,
            @Query("subdomain")String subdomain,
            @Query("ftpuser")String ftpuser
    );
    @DELETE(TAG+"/DeleteSubDomain?format=JSON")
    Call<Reply>DeleteSubDomain(
            @Query("key") String key,
            @Query("name")String name,
            @Query("subdomain")String subdomain
    );
    @POST(TAG+"/SetSubDomainftpAccount?format=JSON")
    Call<Reply>SetSubDomainftpAccount(
            @Query("key") String key,
            @Query("name")String name,
            @Query("subdomain")String subdomain,
            @Query("newftpuser")String newftpuser
    );
    //endregion

    //region alias
    @GET(TAG+"/GetDomainAliases?format=JSON")
    Call<Reply>GetDomainAliases(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddDomainAlias?format=JSON")
    Call<Reply>AddDomainAlias(
            @Query("key") String key,
            @Query("name")String name,
            @Query("alias")String alias
    );
    @DELETE(TAG+"/DeleteDomainAlias?format=JSON")
    Call<Reply>DeleteDomainAlias(
            @Query("key") String key,
            @Query("name")String name,
            @Query("alias")String alias
    );
    //endregion

    //region email
    @GET(TAG+"/GetMailList?format=JSON")
    Call<Reply>GetMailList(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddMailBox?format=JSON")
    Call<Reply>AddMailBox(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account,
            @Query("password")String password,
            @Query("quota")String quota,
            @Query("redirect")String redirect,
            @Query("remail")String remail,
            @Query("displayName")String displayname

    );
    @POST(TAG+"/DeleteMailBox?format=JSON")
    Call<Reply>DeleteMailBox(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account
    );
    @POST(TAG+"/ChangeMailBoxPassword?format=JSON")
    Call<Reply>ChangeMailBoxPassword(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account,
            @Query("newpassword")String newpassword
    );
    @POST(TAG+"/ChangeMailBoxQuota?format=JSON")
    Call<Reply>ChangeMailBoxQuota(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account,
            @Query("quota")String quota
    );
    //endregion

    //region database
    @GET(TAG+"/GetDatabaseList?format=JSON")
    Call<Reply>GetDatabaseList(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddDatabase?format=JSON")
    Call<Reply>AddDatabase(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("username")String username,
            @Query("password")String password,
            @Query("quota")String quota,
            @Query("host")String host
    );
    @DELETE(TAG+"/DeleteDatabase?format=JSON")
    Call<Reply>DeleteDatabase(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database
    );
    @POST(TAG+"/SetDatabaseQuota?format=JSON")
    Call<Reply>SetDatabaseQuota(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("quota")String quota
    );
    //region database user
    @POST(TAG+"/AddDatabaseUser?format=JSON")
    Call<Reply>AddDatabaseUser(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("username")String username,
            @Query("password")String password,
            @Query("host")String host
    );
    @DELETE(TAG+"/DeleteDatabaseUser?format=JSON")
    Call<Reply>DeleteDatabaseUser(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("username")String username
    );
    @POST(TAG+"/ChangeDatabaseUserPassword?format=JSON")
    Call<Reply>ChangeDatabaseUserPassword(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("username")String username,
            @Query("newpassword")String newpassword
    );
    @POST(TAG+"/SetDatabaseUserPermissions?format=JSON")
    Call<Reply>SetDatabaseUserPermissions(
            @Query("key") String key,
            @Query("name")String name,
            @Query("dbtype")String dbtype,
            @Query("database")String database,
            @Query("username")String username,
            @Query("permissions")String permissions
    );
    //endregion
    //endregion

    //region ftp
    @GET(TAG+"/GetFtpAccounts?format=JSON")
    Call<Reply>GetFtpAccounts(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddFtpAccount?format=JSON")
    Call<Reply>AddFtpAccount(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account,
            @Query("password")String password,
            @Query("homePath")String homePath,
            @Query("ronly")String ronly
    );
    @POST(TAG+"/DeleteFtpAccount?format=JSON")
    Call<Reply>DeleteFtpAccount(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account
    );
    @POST(TAG+"/ChangeFtpPassword?format=JSON")
    Call<Reply>ChangeFtpPassword(
            @Query("key") String key,
            @Query("name")String name,
            @Query("account")String account,
            @Query("newpassword")String newpassword,
            @Query("suppress_password_policy")String suppress_password_policy
    );
    //endregion

    //region dns
    @GET(TAG+"/GetDnsRecords?format=JSON")
    Call<Reply>GetDnsRecords(
            @Query("key") String key,
            @Query("name")String name
    );
    @POST(TAG+"/AddDnsRecord?format=JSON")
    Call<Reply>AddDnsRecord(
            @Query("key") String key,
            @Query("name")String name,
            @Query("rec_type")String account,
            @Query("rec_name")String rec_name,
            @Query("rec_value")String rec_value,
            @Query("priority")String priority
    );
    @POST(TAG+"/DeleteDnsRecord?format=JSON")
    Call<Reply>DeleteDnsRecord(
            @Query("key") String key,
            @Query("name")String name,
            @Query("rec_type")String rec_type,
            @Query("rec_name")String rec_name,
            @Query("rec_value")String rec_value,
            @Query("priority")String priority
    );
    @POST(TAG+"/SetDnsZone?format=JSON")
    Call<Reply>SetDnsZone(
            @Query("key") String key,
            @Query("name")String name,
            @Query("soa_expired")String account,
            @Query("soa_ttl")String soa_ttl,
            @Query("soa_refresh")String soa_refresh,
            @Query("soa_email")String soa_email,
            @Query("soa_retry")String soa_retry,
            @Query("soa_serial")String soa_serial,
            @Query("primaryServer")String primaryServer,
            @Query("record")String record,
            @Query("suppress_host_ip")String suppress_host_ip
    );
    //endregion

}
