package com.management.cmdb.services.aggregator.syslog.job;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConnectedAssetServiceTest {

    @Test
    void shouldReturnHostname() {
        assertNotEquals("127.0.0.1", ConnectedAssetService.resolveHostname("127.0.0.1"));
    }
    @Test
    void shouldNotReturnHostname() {
        assertEquals("0.0.0.1", ConnectedAssetService.resolveHostname("0.0.0.1"));
    }

}