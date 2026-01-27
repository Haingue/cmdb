package com.management.cmdb.services.inventory.dto;

public record NotificationDto (
    AuthorDto author,
    NotificationType type,
    String message,
    Object content
    ){

    public enum NotificationType {
        NEW_ITEM, UPDATE_ITEM, DELETE_ITEM,
        NEW_ITEM_TYPE, UPDATE_ITEM_TYPE, DELETE_ITEM_TYPE,
        ADD_ATTRIBUTE, UPDATE_ATTRIBUTE, REMOVE_ATTRIBUTE,
        NEW_LINK, UPDATE_LINK, DELETE_LINK,
        NEW_LINK_TYPE, UPDATE_LINK_TYPE, DELETE_LINK_TYPE
    }
}
