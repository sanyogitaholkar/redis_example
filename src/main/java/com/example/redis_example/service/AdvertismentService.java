package com.example.redis_example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.repo.AdsRepository;

@Service
public class AdvertismentService {

    @Autowired
    private AdsRepository repository;

    public void addAds(List<Advertisments> adsList) {

        try {

            repository.saveAll(adsList);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
