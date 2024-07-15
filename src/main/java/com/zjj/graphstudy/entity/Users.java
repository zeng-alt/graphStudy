package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月10日 22:32
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String userName;

    private String email;

    private String password;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<UserRole> userRoles = new LinkedHashSet<>();

}
