package src.test.java.service;

import dataaccess.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class BTest {

    private Data DataAccessObject;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataAccessObject = new Data(DataType.MEM_DATA);
    }

    @Test
    @DisplayName("Constructs BaseS successfully with a valid DataAccess object")
    public void testBaseS_ValidDataAccess() throws NoSuchFieldException, IllegalAccessException {
        BaseS baseService = new BaseS(DataAccessObject);
        Field dataAccessField = BaseS.class.getDeclaredField("dataAccess");
        dataAccessField.setAccessible(true);
        Data dataAccessValue = (Data) dataAccessField.get(baseService);

        assertNotNull(dataAccessValue);
        assertEquals(DataAccessObject, dataAccessValue);
    }

    @Test
    @DisplayName("Constructs BaseS with a null DataAccess object")
    public void testBaseS_NullDataAccess() throws NoSuchFieldException, IllegalAccessException {
        BaseS baseService = new BaseS(null);
        Field dataAccessField = BaseS.class.getDeclaredField("dataAccess");
        dataAccessField.setAccessible(true);
        Data dataAccessValue = (Data) dataAccessField.get(baseService);

        assertNull(dataAccessValue);
    }
}