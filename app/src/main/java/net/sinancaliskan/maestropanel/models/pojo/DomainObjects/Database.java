package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import java.io.Serializable;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Name",
        "DiskQuota",
        "DiskUsage",
        "Collation",
        "DbType",
        "Users"
})
public class Database implements Serializable {
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("DiskQuota")
    public Integer diskQuota;
    @JsonProperty("DiskUsage")
    public Integer diskUsage;
    @JsonProperty("Collation")
    public String collation;
    @JsonProperty("DbType")
    public String dbType;
    @JsonProperty("Users")
    public ArrayList<DatabaseUser> users = null;

}