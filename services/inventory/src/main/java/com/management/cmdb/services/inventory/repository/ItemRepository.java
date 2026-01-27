package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends ReactiveMongoRepository<ItemEntity, UUID> {

    Mono<Boolean> existsByNameAndTypeLabel(String name, String typeLabel);

    Page<ItemEntity> searchAllByNameContainingIgnoreCaseOrTypeLabel(String nameRegex, String typeLabel, Pageable page);

    Flux<ItemEntity> searchAllByTypeLabelLike(String typeLabel);

    Optional<ItemEntity> findFirstByName(String name);
}