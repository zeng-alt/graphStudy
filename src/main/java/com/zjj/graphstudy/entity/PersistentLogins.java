package com.zjj.graphstudy.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月19日 21:49
 * @version 1.0
 */
@Table(name = "persistent_logins")
@Entity
@Getter
@Setter
public class PersistentLogins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String series;

    private String token;

    private Date lastUsed;
}
