package com.mobelite.publisherManagementSystem.enums;

public enum PublicationType {
    BOOK,
    MAGAZINE,
    UNKNOWN;

    public static PublicationType fromString(String type) {
        try {
            return PublicationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}