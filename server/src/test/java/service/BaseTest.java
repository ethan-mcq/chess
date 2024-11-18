package src.test.java.dataaccess.service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseTest {

    private Data DataAccessObject;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccessObject = new Data(DataType.MEM_DATA);
    }

    @Test
    @DisplayName("Constructs BaseS successfully with a valid DataAccess object")
    public void testBaseS_ValidDataAccess() {
        BaseS baseService = new BaseS(DataAccessObject);
        assertNotNull(baseService.dataAccess);
        assertEquals(DataAccessObject, baseService.dataAccess);
    }

    @Test
    @DisplayName("Constructs BaseS with a null DataAccess object")
    public void testBaseS_NullDataAccess() {
        BaseS baseService = new BaseS(null);
        assertNull(baseService.dataAccess);
    }
}