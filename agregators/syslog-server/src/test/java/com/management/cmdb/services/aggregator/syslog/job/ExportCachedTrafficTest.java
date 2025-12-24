package com.management.cmdb.services.aggregator.syslog.job;

import com.management.cmdb.services.aggregator.syslog.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class ExportCachedTrafficTest {

    @Test
    void shouldReturnHostname() {
        assertNotEquals("127.0.0.1", ItemService.resolveHostname("127.0.0.1"));
    }
    @Test
    void shouldNotReturnHostname() {
        assertEquals("0.0.0.1", ItemService.resolveHostname("0.0.0.1"));
    }

}