package com.management.cmdb.services.inventory.repository;

import com.management.cmdb.services.inventory.model.Project;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<Project, UUID> {
}
