package com.example.redis_example.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.redis_example.entity.Advertisments;

@Repository
public interface AdsRepository extends JpaRepository<Advertisments, Long> {
}
