package com.stavre.tinyurl.repository;

import com.stavre.tinyurl.entity.LinkUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;
import java.util.UUID;

public interface LinkUserRepository extends JpaRepository<LinkUser, Long> {

    @Override
    @PreAuthorize("#entity.userName == authentication.principal.username")
    <S extends LinkUser> S save(S entity);

    @PreAuthorize("#userName == authentication.principal.username")
    long countByUserNameIs(String userName);

    @PreAuthorize("#userName == authentication.principal.username")
    @Query(value = "SELECT COUNT(*) as active_links_count "
            + "FROM Link l "
            + "JOIN Link_User lu ON l.short_link_id = lu.short_link_id "
            + "WHERE lu.user_name = :userName "
            + "  AND l.ACTIVE_FROM <= CURRENT_TIMESTAMP "
            + "  AND l.ACTIVE_UNTIL > CURRENT_TIMESTAMP;", nativeQuery = true)
    long countActiveLinksByUserName(@Param("userName") String username);

    @PreAuthorize("#userName == authentication.principal.username")
    @Query(value = "SELECT COUNT(*) as active_links_count "
            + "FROM Link l "
            + "JOIN Link_User lu ON l.short_link_id = lu.short_link_id "
            + "WHERE lu.user_name = :userName "
            + "  AND (l.ACTIVE_FROM > CURRENT_TIMESTAMP "
            + "  OR l.ACTIVE_UNTIL < CURRENT_TIMESTAMP);", nativeQuery = true)
    long countExpiredLinksByUserName(@Param("userName") String username);

    void deleteLinkUserEntityByShortLinkId(UUID linkId);

    Optional<LinkUser> findLinkUserByUserNameAndShortLinkId(String username, UUID linkId);
}
