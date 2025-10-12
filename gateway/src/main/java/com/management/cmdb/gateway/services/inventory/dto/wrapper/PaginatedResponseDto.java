package com.management.cmdb.gateway.services.inventory.dto.wrapper;

import java.util.List;

public record PaginatedResponseDto<T> (
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
){

    public boolean isEmpty() {
        return content.isEmpty();
    }
}