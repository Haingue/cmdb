package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemTypeRepository extends CrudRepository<ItemTypeEntity, UUID> {
    Optional<ItemTypeEntity> findFirstByLabel(String label);

    Page<ItemTypeEntity> searchAllByLabelContainingIgnoreCase(String label, Pageable page);
}
