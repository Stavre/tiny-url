package com.stavre.tinyurl.repository.authenticateduser;

import com.stavre.tinyurl.entity.authenticateduser.AuthenticatedUserLinkEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthenticatedUserLinkRepository extends JpaRepository<AuthenticatedUserLinkEntity, UUID> {

    Optional<AuthenticatedUserLinkEntity> findLinkByShortLinkId(UUID shortLinkId);

    void deleteAllByShortLinkId(UUID shortLinkId);

    @Query(value = "Select * from Authenticated_User_Link_Entity where short_link_id in :uuids;", nativeQuery = true)
    List<AuthenticatedUserLinkEntity> findAllLinksByShortLinkIds(@NonNull Iterable<UUID> uuids);
}
