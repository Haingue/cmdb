package com.management.cmdb.services.inventory.dto.wrapper;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PaginatedResponseDto<T> (
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean last
){

    public static <Dto, Entity> PaginatedResponseDto<Dto> toPaginatedDto(List<Entity> entityList, Function<Entity, Dto> mapper) {
        List<Dto> content = entityList.stream()
                .map(mapper)
                .toList();
        return new PaginatedResponseDto<Dto>(
                content,
                1,
                entityList.size(),
                entityList.size(),
                1,
                true
        );
    }

    /**
     * TODO test or remove
     * @param page
     * @param mapper
     * @return
     * @param <Dto>
     */
    public static <Dto, Entity> PaginatedResponseDto<Dto> toPaginatedDto(Page<Entity> page, Function<Entity, Dto> mapper) {
        List<Dto> content = page.getContent().stream()
                .map(mapper)
                .toList();
        return new PaginatedResponseDto<Dto>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}