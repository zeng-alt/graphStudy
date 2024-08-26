package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 14:35
 */
@Getter
@Setter
@Entity
@Table(name = "resources")
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String resourceUrl;

    private String requestMade;

    private String requestProtocol;

    @OneToMany(mappedBy = "resource", orphanRemoval = true)
    private Set<Expressions> expressions = new LinkedHashSet<>();


}
