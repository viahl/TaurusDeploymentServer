package se.effectivecode.tools.deploymentserver.io.rest.helper;

import com.google.gson.Gson;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RESTHelper {
    private Gson gson = new Gson();

    public String toJSON(Object src){
        return this.gson.toJson(src);
    }
}
