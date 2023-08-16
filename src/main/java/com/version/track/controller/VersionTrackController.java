package com.version.track.controller;


import com.version.track.service.VersionTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/versions")
public class VersionTrackController {
    @Autowired
    private VersionTrackService versionTrackService;
    @GetMapping("/fetch")
    public ResponseEntity<String> getVersionNumber() {
       return new ResponseEntity<>(versionTrackService.getVersionNumbers(), HttpStatus.OK);
    }
}
