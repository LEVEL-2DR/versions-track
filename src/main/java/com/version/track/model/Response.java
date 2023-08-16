package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Response{
    public int numFound;
    public int start;
    public ArrayList<Doc> docs;
}
