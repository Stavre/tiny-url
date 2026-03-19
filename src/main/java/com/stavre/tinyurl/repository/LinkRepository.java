package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findLinkByShortLinkId(UUID shortLinkId);

    void deleteAllByShortLinkId(UUID shortLinkId);

    @PreAuthorize("#username == authentication.principal.username")
    @Query(value = "SELECT l.* "
            + "FROM Link l "
            + "JOIN Link_User lu "
            + "  ON l.short_link_id = lu.short_link_id "
            + "WHERE lu.user_name = :username;", nativeQuery = true)
    List<Link> findUserLinks(String username);

    @Query(value = "SELECT l.* "
            + "FROM Link l "
            + "WHERE l.original_Url = :originalUrl "
            + "  AND NOT EXISTS ( "
            + "    SELECT 1"
            + "    FROM Link_User lu "
            + "    WHERE lu.short_link_id = l.short_Link_Id "
            + "  );", nativeQuery = true)
    Optional<Link> findAnonymousLinkByOriginalUrl(String originalUrl);
}

