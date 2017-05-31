package net.sinancaliskan.maestropanel.models.pojo.ServerObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Name",
        "Host",
        "ComputerName",
        "OperatingSystem",
        "Version",
        "Cpu",
        "Status"
})
public class Server implements Serializable{

    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Host")
    public String host;
    @JsonProperty("ComputerName")
    public String computerName;
    @JsonProperty("OperatingSystem")
    public String operatingSystem;
    @JsonProperty("Version")
    public String version;
    @JsonProperty("Cpu")
    public String cpu;
    @JsonProperty("Status")
    public Boolean status;

}