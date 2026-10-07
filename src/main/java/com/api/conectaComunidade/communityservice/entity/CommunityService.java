package com.api.conectaComunidade.communityservice.entity;

import com.api.conectaComunidade.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "community_services")
public class CommunityService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceArea area;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime horarioInicio;

    @Column(nullable = false)
    private String local;

    @Column(nullable = false)
    private Integer vagas;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contributor_id", nullable = false)
    private User contributor;

}
