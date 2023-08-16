package com.version.track.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Spellcheck{
    public ArrayList<Object> suggestions;
}
