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
@Table(
        name = "role_permission",
        indexes = {
            @Index(name = "idx_permission_role_id", columnList = "role_id, permission_id", unique = true)
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permissions permission;

}
