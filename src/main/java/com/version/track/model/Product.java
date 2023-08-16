package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product{
    public String cycle;
    public String lts;
    public String releaseDate;
    public String support;
    public String eol;
    public String latest;
    public String link;
}