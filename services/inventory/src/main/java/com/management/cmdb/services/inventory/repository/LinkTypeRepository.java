package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface LinkTypeRepository extends ReactiveMongoRepository<LinkTypeEntity, UUID> {

    Mono<Void> existsByLabelIgnoreCase(String label);

    Mono<LinkTypeEntity> findFirstByLabelIgnoreCase(String label);

    Page<LinkTypeEntity> searchAllByLabelContainingIgnoreCase(String label, Pageable page);
}
