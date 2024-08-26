package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 14:36
 */
@Getter
@Setter
@Entity
@Table(name = "expressions")
public class Expressions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String expression;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resources resource;

}
