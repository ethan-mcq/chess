package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class servicesTest {

    private services services;
    private data concreteData;

    @BeforeEach
    public void setUp() {
        concreteData = new data(dataTypes.MEM_DATA);
        services = new services(concreteData);
    }

    @Test
    @DisplayName("Fetch Existing Service")
    public void testFetchClientService_ExistingService() {

        authS authService = services.fetchClientService(authS.class);

        assertNotNull(authService);
        assertTrue(authService instanceof authS);
    }

    @Test
    @DisplayName("Fetch Non-Existing Service")
    public void testFetchClientService_NonExistingService() {

        class MockService extends baseS {
            public MockService(data data) {
                super(data);
            }
        }

        Executable executable = () -> services.fetchClientService(MockService.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Service unavailable: " + MockService.class.getName(), exception.getMessage());
    }

    private void assertServiceNotAvailable(Executable executable, String className) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Service unavailable: " + className, exception.getMessage());
    }
}