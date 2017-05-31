package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "pinned",
        "Id",
        "Name",
        "Status",
        "ExpirationDate",
        "OwnerName",
        "Email",
        "Disk",
        "IpAddr"
})
public class Domain implements Serializable {
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Status")
    public Integer status;
    @JsonProperty("ExpirationDate")
    public String expirationDate;
    @JsonProperty("OwnerName")
    public String ownerName;
    @JsonProperty("Email")
    public Integer email;
    @JsonProperty("Disk")
    public Integer disk;
    @JsonProperty("IpAddr")
    public Object ipAddr;

}
