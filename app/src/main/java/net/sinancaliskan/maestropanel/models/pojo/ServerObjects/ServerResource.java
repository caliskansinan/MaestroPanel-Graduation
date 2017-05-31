package net.sinancaliskan.maestropanel.models.pojo.ServerObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "resourceType",
        "resourceName",
        "Total",
        "Used",
        "Warning"
})
public class ServerResource implements Serializable{

    @JsonProperty("resourceType")
    public String resourceType;
    @JsonProperty("resourceName")
    public String resourceName;
    @JsonProperty("Total")
    public String total;
    @JsonProperty("Used")
    public String used;
    @JsonProperty("Warning")
    public Object warning;

}