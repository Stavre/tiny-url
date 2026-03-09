package com.stavre.tinyurl.repository.authenticateduser;

import com.stavre.tinyurl.entity.authenticateduser.LinkAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkAccessRepository extends JpaRepository<LinkAccessEntity, Long> {
}
