package com.example.redis_example.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;
import com.example.redis_example.entity.AdClick;

@Component
public class ClickEventQueue {

    // The queue can hold at most 100,000 click events at any given time
    private final BlockingQueue<AdClick> queue = new LinkedBlockingQueue<>(100_000);

    public void add(AdClick event) {
        queue.offer(event); // non-blocking
    }

    public List<AdClick> drain(int batchSize) {
        List<AdClick> batch = new ArrayList<>(batchSize);
        queue.drainTo(batch, batchSize);
        return batch;
    }

}