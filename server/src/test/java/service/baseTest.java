package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class baseTest {

    private data dataAccessObject;

    @BeforeEach
    public void setUp() {
        dataAccessObject = new data(dataTypes.MEM_DATA);
    }

    @Test
    @DisplayName("Constructs baseS successfully with a valid dataAccess object")
    public void testBaseS_ValidDataAccess() {
        baseS baseService = new baseS(dataAccessObject);
        assertNotNull(baseService.dataAccess);
        assertEquals(dataAccessObject, baseService.dataAccess);
    }

    @Test
    @DisplayName("Constructs baseS with a null dataAccess object")
    public void testBaseS_NullDataAccess() {
        baseS baseService = new baseS(null);
        assertNull(baseService.dataAccess);
    }
}