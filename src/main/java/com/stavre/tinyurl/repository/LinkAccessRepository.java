package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.LinkAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkAccessRepository extends JpaRepository<LinkAccess, Long> {
}
