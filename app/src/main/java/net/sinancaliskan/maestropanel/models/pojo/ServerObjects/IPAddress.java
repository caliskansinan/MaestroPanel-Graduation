package net.sinancaliskan.maestropanel.models.pojo.ServerObjects;

/**
 * Created by SinanCaliskan on 26.05.2017.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "ServerId",
        "NicId",
        "NicName",
        "IpAddr",
        "Subnet",
        "isExclusive",
        "isDedicated",
        "isShared",
        "domainCount"
})
public class IPAddress {

    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("ServerId")
    public Integer serverId;
    @JsonProperty("NicId")
    public Integer nicId;
    @JsonProperty("NicName")
    public String nicName;
    @JsonProperty("IpAddr")
    public String ipAddr;
    @JsonProperty("Subnet")
    public String subnet;
    @JsonProperty("isExclusive")
    public Boolean isExclusive;
    @JsonProperty("isDedicated")
    public Boolean isDedicated;
    @JsonProperty("isShared")
    public Boolean isShared;
    @JsonProperty("domainCount")
    public Integer domainCount;

}

