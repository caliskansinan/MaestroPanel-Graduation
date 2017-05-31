package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Username",
        "Password",
        "HomePath",
        "Status",
        "ReadOnly"
})
public class FtpUser implements Serializable{
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Username")
    public String username;
    @JsonProperty("Password")
    public String password;
    @JsonProperty("HomePath")
    public String homePath;
    @JsonProperty("Status")
    public Boolean status;
    @JsonProperty("ReadOnly")
    public Boolean readOnly;

}