package service;

import dataaccess.data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class servicesTest {

    private services services;
    private data mockData;

    @BeforeEach
    public void setUp() {
        mockData = mock(data.class);
        services = new services(mockData);
    }

    @Test
    @DisplayName("Fetch Existing Service")
    public void testFetchClientService_ExistingService() {
        // Positive Test Case: Service exists

        authS authService = services.fetchClientService(authS.class);

        assertNotNull(authService);
        assertTrue(authService instanceof authS);
    }

    @Test
    @DisplayName("Fetch Non-Existing Service")
    public void testFetchClientService_NonExistingService() {
        // Negative Test Case: Service does not exist

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