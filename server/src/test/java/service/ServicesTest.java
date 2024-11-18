package src.test.java.service;

import dataaccess.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class ServicesTest {

    private Services services;
    private Data concreteData;

    @BeforeEach
    public void setUp() throws DataAccessException {
        concreteData = new Data(DataType.MEM_DATA);
        services = new Services(concreteData);
    }

    @Test
    @DisplayName("Fetch Existing Service")
    public void testFetchClientServiceExistingService() {

        AuthS authService = services.fetchClientService(AuthS.class);

        assertNotNull(authService);
        assertTrue(authService instanceof AuthS);
    }

    @Test
    @DisplayName("Fetch Non-Existing Service")
    public void testFetchClientServiceNonExistingService() {

        class MockService extends BaseS {
            public MockService(Data data) {
                super(data);
            }
        }

        Executable executable = () -> services.fetchClientService(MockService.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Service unavailable: " + MockService.class.getName(), exception.getMessage());
    }

}