package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseHeader{
    public int status;
    @JsonProperty("QTime")
    public int qTime;
    public Params params;
}