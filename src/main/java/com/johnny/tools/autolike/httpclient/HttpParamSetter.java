package com.johnny.tools.autolike.httpclient;

import org.apache.http.params.HttpParams;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by weffen on 15/4/14.
 */
public class HttpParamSetter {
    private HttpParams httpParams;

    public void setHttpParams(HttpParams httpParams) {
        this.httpParams = httpParams;
    }

    private Map<String, Object> parameters;

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @PostConstruct
    public void applyParameters() {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            httpParams.setParameter(entry.getKey(), entry.getValue());
        }

    }
}
