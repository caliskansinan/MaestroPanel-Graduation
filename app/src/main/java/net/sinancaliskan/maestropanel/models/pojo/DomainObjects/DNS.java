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
        "ZoneType",
        "Name",
        "AllowZoneTransfers",
        "SecondaryServers",
        "SerialNumber",
        "PrimaryServer",
        "ResponsiblePerson",
        "RefreshInterval",
        "RetryInterval",
        "Expires",
        "TTL",
        "Records"
})
public class DNS implements Serializable{
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("ZoneType")
    public Integer zoneType;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("AllowZoneTransfers")
    public Boolean allowZoneTransfers;
    @JsonProperty("SecondaryServers")
    public Object secondaryServers;
    @JsonProperty("SerialNumber")
    public String serialNumber;
    @JsonProperty("PrimaryServer")
    public String primaryServer;
    @JsonProperty("ResponsiblePerson")
    public String responsiblePerson;
    @JsonProperty("RefreshInterval")
    public Integer refreshInterval;
    @JsonProperty("RetryInterval")
    public Integer retryInterval;
    @JsonProperty("Expires")
    public Integer expires;
    @JsonProperty("TTL")
    public Integer TTL;
    @JsonProperty("Records")
    public ArrayList<DNSRecord> records = null;

}