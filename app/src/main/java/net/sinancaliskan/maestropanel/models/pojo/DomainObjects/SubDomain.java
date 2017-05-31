package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Name",
        "FtpUser",
        "Supports"
})
public class SubDomain implements Serializable{
    @JsonProperty("Id")
    public long id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("FtpUser")
    public String ftpUser;
    @JsonProperty("Supports")
    public String supports;
}
