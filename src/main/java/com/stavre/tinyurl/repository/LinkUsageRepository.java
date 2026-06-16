package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.LinkUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LinkUsageRepository extends JpaRepository<LinkUsage, Long> {

    List<LinkUsage> findByShortLinkIdOrderByUsedAtAsc(String shortLinkId);
}
