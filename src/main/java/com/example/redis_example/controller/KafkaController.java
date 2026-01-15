package com.example.redis_example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.service.AdvertismentService;
import com.example.redis_example.service.KafkaProducerService;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService producer;

    public KafkaController(KafkaProducerService producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public String send(@RequestParam String message) {
        producer.sendMessage(message);
        return "Message sent to Kafka";
    }
}
