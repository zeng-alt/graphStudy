package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

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
@Table(name = "roles")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String roleName;

    private String roleKey;

    @Comment("基数约束")
    private Integer cardinalityConstraint;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private Roles parentRole;

    @OneToMany(mappedBy = "roles", orphanRemoval = true)
    private Set<RoleExclusive> roleExclusives = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "role", orphanRemoval = true)
    private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "user_group_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id"))
    private Set<UserGroups> roleGroups = new LinkedHashSet<>();

}
