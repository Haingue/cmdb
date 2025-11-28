package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.LinkTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkTypeRepository extends CrudRepository<LinkTypeEntity, UUID> {
    Optional<LinkTypeEntity> findFirstByLabelIgnoreCase(String label);

    Page<LinkTypeEntity> searchAllByLabelContainingIgnoreCase(String label, Pageable page);
}
