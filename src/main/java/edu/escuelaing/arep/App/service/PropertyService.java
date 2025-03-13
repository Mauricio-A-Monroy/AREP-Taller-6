package edu.escuelaing.arep.App.service;

import edu.escuelaing.arep.App.model.Property;
import edu.escuelaing.arep.App.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Property not found with ID: " + id));
    }

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property updatedProperty = propertyRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Property not found with ID: " + id));


        if (propertyDetails.getAddress() != null){
            updatedProperty.setAddress(propertyDetails.getAddress());
        }

        if (propertyDetails.getPrice() != null){
            updatedProperty.setPrice(propertyDetails.getPrice());
        }

        if (propertyDetails.getSize() != null){
            updatedProperty.setSize(propertyDetails.getSize());
        }

        if (propertyDetails.getDescription() != null){
            updatedProperty.setDescription(propertyDetails.getDescription());
        }

        return propertyRepository.save(updatedProperty);
    }

    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Property with ID " + id + " not found");
        }
        propertyRepository.deleteById(id);
    }
}
