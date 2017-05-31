package net.sinancaliskan.maestropanel.models.pojo.ResellerObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "pinned",
        "Nic",
        "IpAddr",
        "isShared",
        "isDedicated",
        "isExclusive"
})
public class IPAddress {
    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Nic")
    public String nic;
    @JsonProperty("IpAddr")
    public String ipAddr;
    @JsonProperty("isShared")
    public Boolean isShared;
    @JsonProperty("isDedicated")
    public Boolean isDedicated;
    @JsonProperty("isExclusive")
    public Boolean isExclusive;

}

