package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 13:57
 */
@Getter
@Setter
@Entity
@Table(name = "role_exclusive")
public class RoleExclusive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roles_id")
    private Roles roles;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "exclusive_role_id")
    private Roles exclusiveRole;
}
