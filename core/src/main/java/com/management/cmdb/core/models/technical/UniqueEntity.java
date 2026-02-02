package com.management.cmdb.core.models.technical;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UniqueEntity implements Serializable {

    private UUID uuid;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UniqueEntity that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "uuid=" + uuid +
                '}';
    }
}
