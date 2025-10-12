package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

    boolean existsByNameAndTypeLabel(String name, String typeLabel);

    Page<ItemEntity> searchAllByNameContainingIgnoreCaseOrTypeLabel(String nameRegex, String typeLabel, Pageable page);

    Optional<ItemEntity> findFirstByName(String name);
}