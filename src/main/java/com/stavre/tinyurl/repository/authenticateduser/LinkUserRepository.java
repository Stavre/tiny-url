package com.stavre.tinyurl.repository.authenticateduser;

import com.stavre.tinyurl.entity.authenticateduser.LinkUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.UUID;

public interface LinkUserRepository extends JpaRepository<LinkUserEntity, Long> {

    @Override
    @PreAuthorize("#entity.userName == authentication.principal.username")
    <S extends LinkUserEntity> S save(S entity);

    List<LinkUserEntity> getLinkUserEntitiesByUserName(String userName);

    @PreAuthorize("#userName == authentication.principal.username")
    long countByUserNameIs(String userName);

    @PreAuthorize("#userName == authentication.principal.username")
    @Query(value = "SELECT COUNT(*) as active_links_count "
            + "FROM Authenticated_User_Link_Entity l "
            + "JOIN Link_User_Entity lu ON l.short_link_id = lu.link_id "
            + "WHERE lu.user_name = :userName "
            + "  AND l.valid_from <= CURRENT_TIMESTAMP "
            + "  AND l.valid_until > CURRENT_TIMESTAMP;", nativeQuery = true)
    long countActiveLinksByUserName(@Param("userName") String username);

    @PreAuthorize("#userName == authentication.principal.username")
    @Query(value = "SELECT COUNT(*) as active_links_count "
            + "FROM Authenticated_User_Link_Entity l "
            + "JOIN Link_User_Entity lu ON l.short_link_id = lu.link_id "
            + "WHERE lu.user_name = :userName "
            + "  AND (l.valid_from > CURRENT_TIMESTAMP "
            + "  OR l.valid_until < CURRENT_TIMESTAMP);", nativeQuery = true)
    long countExpiredLinksByUserName(@Param("userName") String username);

    void deleteLinkUserEntityByLinkId(UUID linkId);
}
