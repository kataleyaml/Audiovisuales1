package modelo;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class DetallePrestamoPK implements Serializable {

    // Los nombres de los campos deben coincidir con las columnas de la tabla detalle_prestamo
    private int ID_Prestamo;
    private int ID_Activo; 

    public DetallePrestamoPK() {
    }

    public DetallePrestamoPK(int ID_Prestamo, int ID_Activo) {
        this.ID_Prestamo = ID_Prestamo;
        this.ID_Activo = ID_Activo;
    }

    // Getters y Setters (Necesarios)
    public int getID_Prestamo() { return ID_Prestamo; }
    public void setID_Prestamo(int ID_Prestamo) { this.ID_Prestamo = ID_Prestamo; }

    public int getID_Activo() { return ID_Activo; }
    public void setID_Activo(int ID_Activo) { this.ID_Activo = ID_Activo; }

    // Implementar hashCode() y equals() (Obligatorio en Claves Compuestas JPA)
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) ID_Prestamo;
        hash += (int) ID_Activo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DetallePrestamoPK)) {
            return false;
        }
        DetallePrestamoPK other = (DetallePrestamoPK) object;
        if (this.ID_Prestamo != other.ID_Prestamo) {
            return false;
        }
        if (this.ID_Activo != other.ID_Activo) {
            return false;
        }
        return true;
    }
}