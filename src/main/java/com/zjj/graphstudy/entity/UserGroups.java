package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 14:14
 */
@Getter
@Setter
@Entity
@Table(name = "user_groups")
public class UserGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String groupName;

    @ManyToMany(mappedBy = "userGroups")
    private Set<Users> users = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "roleGroups")
    private Set<Roles> roles = new LinkedHashSet<>();

}
