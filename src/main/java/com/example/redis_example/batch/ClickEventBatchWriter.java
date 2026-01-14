package com.example.redis_example.batch;

import java.util.List;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.redis_example.entity.AdClick;
import com.example.redis_example.queue.ClickEventQueue;
import com.example.redis_example.repo.AdClickRepository;

import jakarta.transaction.Transactional;

@Component
@EnableScheduling
public class ClickEventBatchWriter {

    private final ClickEventQueue queue;
    private final AdClickRepository repository;

    public ClickEventBatchWriter(ClickEventQueue queue,
            AdClickRepository repository) {
        this.queue = queue;
        this.repository = repository;
    }

    //Every 100 ms | Drains up to 1000 events | Saves them in one DB transaction
    @Scheduled(fixedDelay = 100) // every 100 ms
    @Transactional
    public void writeBatch() {
        List<AdClick> batch = queue.drain(1000);

        if (batch.isEmpty())
            return;

        long start = System.nanoTime();
        repository.saveAll(batch);
        long end = System.nanoTime();

        // log.info("Saved {} clicks in {} ms",batch.size(), (end - start) / 1_000_000);
    }

}