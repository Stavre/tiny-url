package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findLinkByShortLinkId(String shortLinkId);

    @Query(value = "SELECT l.* FROM Link l "
            + "WHERE l.short_link_id = :shortLinkId "
            + "  AND (l.active_from IS NULL OR l.active_from <= CURRENT_TIMESTAMP) "
            + "  AND (l.active_until IS NULL OR l.active_until > CURRENT_TIMESTAMP)",
            nativeQuery = true)
    Optional<Link> findActiveLinkByShortLinkId(String shortLinkId);

    void deleteAllByShortLinkId(String shortLinkId);

    @PreAuthorize("#username == authentication.principal.username")
    @Query(value = "SELECT l.* "
            + "FROM Link l "
            + "JOIN Link_User lu "
            + "  ON l.short_link_id = lu.short_link_id "
            + "WHERE lu.user_name = :username;", nativeQuery = true)
    List<Link> findUserLinks(String username);

}
