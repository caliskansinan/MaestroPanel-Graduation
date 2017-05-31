
package net.sinancaliskan.maestropanel.models.pojo.DomainObjects;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "pinned",
        "Name",
        "Quota",
        "Accounts"
})
public class Mail implements Serializable{
    @JsonProperty("Id")
    public int id;
    @JsonProperty("pinned")
    public boolean pinned;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Quota")
    public Integer quota;
    @JsonProperty("Accounts")
    public List<MailAccount> accounts = null;

}