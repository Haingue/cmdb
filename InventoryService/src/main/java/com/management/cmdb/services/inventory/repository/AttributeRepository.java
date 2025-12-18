package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeEntity, UUID> {

    @Modifying
    @Transactional
    void deleteAllByItemUuid(UUID itemUuid);

}
