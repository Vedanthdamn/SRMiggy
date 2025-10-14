package com.srmiggy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    private Double rating = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
