package com.management.cmdb.inventory.service.repository;

import com.management.cmdb.inventory.service.entity.LinkTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkTypeRepository extends CrudRepository<LinkTypeEntity, UUID> {
}
