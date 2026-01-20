package com.management.cmdb.core.models.technical;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
public class UniqueEntity implements Serializable {

    private final UUID uuid;

    protected UniqueEntity() {
        this.uuid = UUID.randomUUID();
    }

    // @Builder(buildMethodName = "reload")
    public UniqueEntity(UUID uuid) {
        assert uuid != null;
        this.uuid = uuid;
    }

    public UniqueEntity(UniqueEntity source) {
        this.uuid = source.uuid;
    }

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
