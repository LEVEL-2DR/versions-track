package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Params{
    public String q;
    public String core;
    public String indent;
    public String spellcheck;
    public String fl;
    public String start;
   // public String spellcheck.count;
    public String sort;
    public String rows;
    public String wt;
    public String version;
}