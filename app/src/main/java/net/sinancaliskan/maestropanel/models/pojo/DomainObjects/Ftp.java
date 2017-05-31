package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Users"
})
public class Ftp implements Serializable {
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Users")
    public ArrayList<FtpUser> users = null;

}