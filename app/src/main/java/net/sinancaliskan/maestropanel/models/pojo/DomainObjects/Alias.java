package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Name"
})

public class Alias implements Serializable {
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Name")
    public String name;
}
