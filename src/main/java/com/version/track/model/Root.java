package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Root{
    public ResponseHeader responseHeader;
    public Response response;
    public Spellcheck spellcheck;
}