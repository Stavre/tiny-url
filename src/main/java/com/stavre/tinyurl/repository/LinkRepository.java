package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.Link;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findLinkByShortLinkId(UUID shortLinkId);

    void deleteAllByShortLinkId(UUID shortLinkId);

    @Query(value = "Select * from Link where short_link_id in :uuids;", nativeQuery = true)
    List<Link> findAllLinksByShortLinkIds(@NonNull Iterable<UUID> uuids);
}
