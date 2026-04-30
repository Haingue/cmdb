package com.management.cmdb.backend.services.dashboard;

public record DashboardMetrics(
    long serverCount,
    long applicationCount,
    long projectCount,
    long activeUserCount
) {}
