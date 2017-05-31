package net.sinancaliskan.maestropanel.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "StatusCode",
        "ErrorCode",
        "Message",
        "Details"
})
public class Reply {

    @JsonProperty("StatusCode")
    public Integer statusCode;
    @JsonProperty("ErrorCode")
    public Integer errorCode;
    @JsonProperty("Message")
    public String message;
    @JsonProperty("Details")
    public Object details;

}