package com.management.cmdb.core.models.business.project;

import com.management.cmdb.core.models.technical.VersionedSavedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Link extends VersionedSavedEntity {
    private Project project;
    private BusinessService businessService;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Link link)) return false;
        return super.equals(link) && Objects.equals(project, link.project) && Objects.equals(businessService, link.businessService);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), project, businessService);
    }
}