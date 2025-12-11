package modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entidad DetallePrestamo con clave primaria compuesta (ID_Prestamo, ID_Activo).
 */
@Entity
@Table(name = "detalle_prestamo")
@NamedQueries({
    @NamedQuery(name = "DetallePrestamo.findAll", query = "SELECT d FROM DetallePrestamo d")
})
public class DetallePrestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===========================================
    // 1. CLAVE PRIMARIA COMPUESTA (@EmbeddedId)
    // ===========================================
    @EmbeddedId
    protected modelo.DetallePrestamoPK detallePrestamoPK;

    // Relación Prestamo (usando las columnas de la PK compuesta)
    @JoinColumn(name = "ID_Prestamo", referencedColumnName = "ID_Prestamo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Prestamo prestamo; 

    // Relación Equipos (usando las columnas de la PK compuesta)
    @JoinColumn(name = "ID_Activo", referencedColumnName = "ID_Equipo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Equipos equipos; 

    // ===========================================
    // 2. CAMPOS DE COPIA (Metadata del Equipo)
    // Coinciden con la tabla
    // ===========================================
    @Basic(optional = false)
    @Column(name = "Nombre_Equipo")
    private String nombreEquipo;

    @Column(name = "Marca") 
    private String marca;

    @Basic(optional = false)
    @Column(name = "Modelo_Serie")
    private String modeloSerie;

    // ===========================================
    // 3. CAMPOS ELIMINADOS
    // Se eliminó la referencia a 'observaciones_devolucion' y 'Estado_Devolucion'
    // ===========================================

    // ===========================================
    // 4. Constructores
    // ===========================================
    
    // Constructor vacío (necesario para JPA)
    public DetallePrestamo() {
    }

    // Constructor que inicializa la PK (puede ser útil para crear el proxy)
    public DetallePrestamo(modelo.DetallePrestamoPK detallePrestamoPK) {
        this.detallePrestamoPK = detallePrestamoPK;
    }

    // Constructor para inicializar la PK y campos requeridos
    public DetallePrestamo(modelo.DetallePrestamoPK detallePrestamoPK, String nombreEquipo, String modeloSerie) {
        this.detallePrestamoPK = detallePrestamoPK;
        this.nombreEquipo = nombreEquipo;
        this.modeloSerie = modeloSerie;
    }
    
    // Constructor con IDs para simplificar la creación de la PK
    public DetallePrestamo(int idPrestamo, int idActivo) {
        this.detallePrestamoPK = new modelo.DetallePrestamoPK(idPrestamo, idActivo);
    }
    
    // ===========================================
    // 5. Getters y Setters
    // ===========================================

    public modelo.DetallePrestamoPK getDetallePrestamoPK() {
        return detallePrestamoPK;
    }

    public void setDetallePrestamoPK(modelo.DetallePrestamoPK detallePrestamoPK) {
        this.detallePrestamoPK = detallePrestamoPK;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Equipos getEquipos() {
        return equipos;
    }

    public void setEquipos(Equipos equipos) {
        this.equipos = equipos;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModeloSerie() {
        return modeloSerie;
    }

    public void setModeloSerie(String modeloSerie) {
        this.modeloSerie = modeloSerie;
    }

    // Se eliminaron Getters/Setters de 'observacionesDevolucion' y 'estadoDevolucion'

    // ===========================================
    // 6. Hashcode y Equals (Basados en la PK Compuesta)
    // ===========================================

    @Override
    public int hashCode() {
        return Objects.hash(detallePrestamoPK);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DetallePrestamo other = (DetallePrestamo) object;
        return Objects.equals(this.detallePrestamoPK, other.detallePrestamoPK);
    }

    @Override
    public String toString() {
        return "modelo.DetallePrestamo[ detallePrestamoPK=" + detallePrestamoPK + " ]";
    }
}