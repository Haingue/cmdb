package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.AttributeEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeRepository extends ReactiveMongoRepository<AttributeEntity, UUID> {

}
