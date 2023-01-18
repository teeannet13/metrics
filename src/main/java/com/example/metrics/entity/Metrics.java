package com.example.metrics.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "metric")
@Data
@NoArgsConstructor
public class Metrics {
    @SequenceGenerator(name = "metric_id_seq", sequenceName = "metric_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "metric_id_seq")
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ms_request_pending")
    private Long msRequestPending;

    @Column(name = "ms_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date msDateTime;
}
