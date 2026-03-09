package com.stavre.tinyurl.repository.anonymoususer;

import com.stavre.tinyurl.entity.anonymoususer.AnonymousLinkEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnonymousUserLinkRepository extends JpaRepository<AnonymousLinkEntity, UUID> {

    Optional<AnonymousLinkEntity> findLinkById(UUID id);

    List<AnonymousLinkEntity> findAllById(@NonNull Iterable<UUID> uuids);
}
