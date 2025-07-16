package com.management.cmdb.inventory.service.repository;

import com.management.cmdb.inventory.service.entity.ItemTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemTypeRepository extends CrudRepository<ItemTypeEntity, UUID> {
    Optional<ItemTypeEntity> findFirstByLabel(String label);
}
