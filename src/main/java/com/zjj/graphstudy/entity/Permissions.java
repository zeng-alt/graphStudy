package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月10日 22:33
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String permsName;

    private String permsKey;

    @OneToMany(mappedBy = "permission", orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

}
