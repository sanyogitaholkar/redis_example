package com.example.redis_example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.service.AdvertismentService;

@RestController
@RequestMapping("/ads")
public class AdsController {

    @Autowired
    private AdvertismentService adsService;

    @PostMapping("/add")
    public ResponseEntity<Void> addAds(@RequestBody List<Advertisments> adsList) {

        adsService.addAds(adsList);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
