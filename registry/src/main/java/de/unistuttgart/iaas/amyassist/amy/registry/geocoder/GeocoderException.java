package de.unistuttgart.iaas.amyassist.amy.registry.geocoder;

/**
 * Exception from the geocoder
 *
 * @author Benno Krauß
 */
public class GeocoderException extends Exception {
    public GeocoderException(String message) {
        super(message);
    }
    public GeocoderException(Exception e) {
        super(e.getMessage());
    }
}
