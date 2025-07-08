package com.management.cmdb.inventory.service.repository;

import com.management.cmdb.inventory.service.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

}