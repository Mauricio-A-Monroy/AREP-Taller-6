package edu.escuelaing.arep.App;

import edu.escuelaing.arep.App.model.Property;
import edu.escuelaing.arep.App.repository.PropertyRepository;
import edu.escuelaing.arep.App.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

    private Property property;

    @BeforeEach
    void setUp() {
        property = new Property(1L, "Calle 123", 300000.0, 120.5, "Casa grande");
    }

    @Test
    void testGetAllProperties() {
        List<Property> properties = Arrays.asList(property, new Property(2L, "Avenida 456", 500000.0, 200.0, "Apartamento"));
        when(propertyRepository.findAll()).thenReturn(properties);

        List<Property> result = propertyService.getAllProperties();

        assertEquals(2, result.size());
        verify(propertyRepository, times(1)).findAll();
    }

    @Test
    void testGetPropertyById_Success() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        Property result = propertyService.getPropertyById(1L);
        assertNotNull(result);
        assertEquals("Calle 123", result.getAddress());
        verify(propertyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPropertyById_NotFound() {
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> propertyService.getPropertyById(99L));
        assertEquals("Property not found with ID: 99", exception.getMessage());
    }

    @Test
    void testCreateProperty() {
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        Property result = propertyService.createProperty(property);
        assertNotNull(result);
        assertEquals("Calle 123", result.getAddress());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdateProperty_Success() {
        Property newDetails = new Property(null, "Avenida 789", 400000.0, 150.0, "Casa moderna");
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        Property updatedProperty = propertyService.updateProperty(1L, newDetails);

        assertEquals("Avenida 789", updatedProperty.getAddress());
        assertEquals(400000.0, updatedProperty.getPrice());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdateProperty_NotFound() {
        when(propertyRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> propertyService.updateProperty(99L, property));
        assertEquals("Property not found with ID: 99", exception.getMessage());
    }

    @Test
    void testDeleteProperty_Success() {
        when(propertyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(propertyRepository).deleteById(1L);

        assertDoesNotThrow(() -> propertyService.deleteProperty(1L));
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProperty_NotFound() {
        when(propertyRepository.existsById(99L)).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> propertyService.deleteProperty(99L));
        assertEquals("Cannot delete: Property with ID 99 not found", exception.getMessage());
    }
}

