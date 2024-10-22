package service;

import dataaccess.data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;


public class baseTest {

    private data mockData;

    @BeforeEach
    public void setUp() {
        mockData = mock(data.class);
    }

    @Test
    @DisplayName("Constructs baseS successfully with a valid dataAccess object")
    public void testBaseS_ValidDataAccess() {
        baseS baseService = new baseS(mockData);
        assertNotNull(baseService.dataAccess);
        assertEquals(mockData, baseService.dataAccess, "Data access object should be the mockData instance");
    }

    @Test
    @DisplayName("Constructs baseS with a null dataAccess object")
    public void testBaseS_NullDataAccess() {
        baseS baseService = new baseS(null);
        assertNull(baseService.dataAccess, "Data access object should be null");
    }
}