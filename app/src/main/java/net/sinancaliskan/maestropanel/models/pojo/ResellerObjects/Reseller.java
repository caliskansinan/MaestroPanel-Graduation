package net.sinancaliskan.maestropanel.models.pojo.ResellerObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "pinned",
        "ApiAccess",
        "Email",
        "ExpirationDate",
        "FirstName",
        "Id",
        "LastName",
        "LoginType",
        "Organization",
        "Status",
        "Username"
})
public class Reseller implements Serializable{
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("ApiAccess")
    public Boolean apiAccess;
    @JsonProperty("Email")
    public String email;
    @JsonProperty("ExpirationDate")
    public String expirationDate;
    @JsonProperty("FirstName")
    public String firstName;
    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("LastName")
    public String lastName;
    @JsonProperty("LoginType")
    public Integer loginType;
    @JsonProperty("Organization")
    public Object organization;
    @JsonProperty("Status")
    public Integer status;
    @JsonProperty("Username")
    public String username;

}