package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import com.management.cmdb.services.inventory.entity.LinkEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LinkRepository extends ReactiveMongoRepository<LinkEntity, UUID> {

    Mono<Boolean> existsBySourceItemAndTargetItem(LinkEntity sourceItem, LinkEntity targetItem);

    Mono<LinkEntity> findFirstBySourceItemAndTargetItem(ItemEntity sourceItem, ItemEntity targetItem);

    Mono<LinkEntity> findFirstBySourceItemUuidAndTargetItemUuidAndLinkTypeLabel(UUID sourceItem, UUID targetItem, String linkType);

    Mono<Void> deleteAllBySourceItemUuidOrTargetItemUuid(UUID sourceItem, UUID targetItem);
}
