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

    private String username;

    private String email;

    private String password;

    private String telephoneNumber;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<UserRole> userRoles = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "users_group_user",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id"))
    private Set<UserGroups> userGroups = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<UserResource> userResources = new LinkedHashSet<>();


    public Users() {
    }

    public Users(String username, String email, String password, String telephoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.telephoneNumber = telephoneNumber;
    }
}
