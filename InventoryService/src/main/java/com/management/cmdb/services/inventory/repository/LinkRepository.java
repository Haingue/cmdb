package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkEntity, UUID> {

    boolean existsBySourceItemAndTargetItem(LinkEntity sourceItem, LinkEntity targetItem);

    Optional<LinkEntity> findFirstBySourceItemAndTargetItem(ItemEntity sourceItem, ItemEntity targetItem);

    Optional<LinkEntity> findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(UUID sourceItem, UUID targetItem, String linkType);

    @Modifying
    @Transactional
    void deleteAllBySourceItemUuidOrTargetItemUuid(UUID sourceItem, UUID targetItem);
}
