package com.example.metrics.repository;

import com.example.metrics.entity.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {

}
