package com.management.cmdb.core.models.business.request;

import com.management.cmdb.core.models.business.identity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Request {
    private final UUID uuid;
    private final User requestor;
    private final Instant requestTimestamp;

    public Request() {
        this.uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        this.requestor = User.UNKNONW;
        this.requestTimestamp = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Request request)) return false;
        return Objects.equals(uuid, request.uuid) && Objects.equals(requestor, request.requestor)
                && Objects.equals(requestTimestamp, request.requestTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, requestor, requestTimestamp);
    }

    @Override
    public String toString() {
        return "Request{" +
                "uuid=" + uuid +
                ", requestor=" + requestor +
                ", requestTimestamp=" + requestTimestamp +
                '}';
    }
}

