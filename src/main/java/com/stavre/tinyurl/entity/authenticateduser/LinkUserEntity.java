package com.stavre.tinyurl.entity.authenticateduser;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(schema = "authenticated_user",  name = "link_user")
public class LinkUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID linkId;
    private String userName;
}
