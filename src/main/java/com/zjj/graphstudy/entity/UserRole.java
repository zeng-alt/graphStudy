package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月10日 22:50
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "user_role", indexes = {
        @Index(name = "idx_user_role_id", columnList = "user_id, role_id")
})
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;


}
