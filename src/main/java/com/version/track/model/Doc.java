package com.version.track.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Doc{
    public String id;
    public String g;
    public String a;
    public String latestVersion;
    public String repositoryId;
    public String p;
    public long timestamp;
    public int versionCount;
    public ArrayList<String> text;
    public ArrayList<String> ec;
}