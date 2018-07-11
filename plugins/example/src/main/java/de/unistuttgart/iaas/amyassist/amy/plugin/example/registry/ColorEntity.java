package de.unistuttgart.iaas.amyassist.amy.plugin.example.registry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceUnit;
import java.util.Objects;

/**
 * Custom entity for testing
 *
 * @author Benno Krauß
 */
@Entity
@PersistenceUnit(unitName="ColorRegistry")
public class ColorEntity {
    @Id
    @GeneratedValue
    private int id;

    private float redComponent;
    private float greenComponent;
    private float blueComponent;

    public int getId() {
        return id;
    }

    public float getRedComponent() {
        return redComponent;
    }

    public void setRedComponent(float redComponent) {
        this.redComponent = redComponent;
    }

    public float getGreenComponent() {
        return greenComponent;
    }

    public void setGreenComponent(float greenComponent) {
        this.greenComponent = greenComponent;
    }

    public float getBlueComponent() {
        return blueComponent;
    }

    public void setBlueComponent(float blueComponent) {
        this.blueComponent = blueComponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorEntity that = (ColorEntity) o;
        return id == that.id &&
                Float.compare(that.redComponent, redComponent) == 0 &&
                Float.compare(that.greenComponent, greenComponent) == 0 &&
                Float.compare(that.blueComponent, blueComponent) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, redComponent, greenComponent, blueComponent);
    }
}
