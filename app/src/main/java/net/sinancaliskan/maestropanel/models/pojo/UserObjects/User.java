package net.sinancaliskan.maestropanel.models.pojo.UserObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "Username",
        "Type",
        "Status",
        "Email",
        "FirstName",
        "LastName",
        "Organization"
})
public class User implements Serializable {

    @JsonProperty("Id")
    public Integer id;
    @JsonProperty("Username")
    public String username;
    @JsonProperty("Type")
    public Integer type;
    @JsonProperty("Status")
    public Integer status;
    @JsonProperty("Email")
    public String email;
    @JsonProperty("FirstName")
    public String firstName;
    @JsonProperty("LastName")
    public String lastName;
    @JsonProperty("Organization")
    public String organization;

}