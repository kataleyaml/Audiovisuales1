package modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Jose
 */
@Entity
@Table(name = "equipos")
@NamedQueries({
    @NamedQuery(name = "Equipos.findAll", query = "SELECT e FROM Equipos e"),
    @NamedQuery(name = "Equipos.findByIDEquipo", query = "SELECT e FROM Equipos e WHERE e.iDEquipo = :iDEquipo"),
    @NamedQuery(name = "Equipos.findByNombre", query = "SELECT e FROM Equipos e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Equipos.findByMarca", query = "SELECT e FROM Equipos e WHERE e.marca = :marca"),
    @NamedQuery(name = "Equipos.findByModeloSerie", query = "SELECT e FROM Equipos e WHERE e.modeloSerie = :modeloSerie"),
    @NamedQuery(name = "Equipos.findByFechaadquisicion", query = "SELECT e FROM Equipos e WHERE e.fechaadquisicion = :fechaadquisicion"),
    @NamedQuery(name = "Equipos.findByEstado", query = "SELECT e FROM Equipos e WHERE e.estado = :estado"),
    @NamedQuery(name = "Equipos.findByUbicacionactual", query = "SELECT e FROM Equipos e WHERE e.ubicacionactual = :ubicacionactual")})
public class Equipos implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "equipos")
    private Collection<DetallePrestamo> detallePrestamoCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_Equipo")
    private Integer iDEquipo;
    @Basic(optional = false)
    @Column(name = "Nombre")
    private String nombre;
    @Column(name = "Marca")
    private String marca;
    @Basic(optional = false)
    @Column(name = "Modelo_Serie")
    private String modeloSerie;
    @Basic(optional = false)
    @Column(name = "Fecha_adquisicion")
    @Temporal(TemporalType.DATE)
    private Date fechaadquisicion;
    @Basic(optional = false)
    @Column(name = "Estado")
    private String estado;
    @Column(name = "Ubicacion_actual")
    private String ubicacionactual;
    
    // 🚩 Nuevo atributo añadido y mapeado
    @Column(name = "Observaciones") 
    private String observaciones;

    public Equipos() {
    }

    public Equipos(Integer iDEquipo) {
        this.iDEquipo = iDEquipo;
    }

    public Equipos(Integer iDEquipo, String nombre, String modeloSerie, Date fechaadquisicion, String estado) {
        this.iDEquipo = iDEquipo;
        this.nombre = nombre;
        this.modeloSerie = modeloSerie;
        this.fechaadquisicion = fechaadquisicion;
        this.estado = estado;
    }

    // --- Getters y Setters ---

    public Integer getIDEquipo() {
        return iDEquipo;
    }

    public void setIDEquipo(Integer iDEquipo) {
        this.iDEquipo = iDEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Date getFechaadquisicion() {
        return fechaadquisicion;
    }

    public void setFechaadquisicion(Date fechaadquisicion) {
        this.fechaadquisicion = fechaadquisicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUbicacionactual() {
        return ubicacionactual;
    }

    public void setUbicacionactual(String ubicacionactual) {
        this.ubicacionactual = ubicacionactual;
    }
    
    // 🚩 Métodos Get/Set de Observaciones corregidos e implementados
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String text) {
        this.observaciones = text; // <-- ¡CORREGIDO!
    }

    // --- Métodos de la Entidad ---
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDEquipo != null ? iDEquipo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipos)) {
            return false;
        }
        Equipos other = (Equipos) object;
        if ((this.iDEquipo == null && other.iDEquipo != null) || (this.iDEquipo != null && !this.iDEquipo.equals(other.iDEquipo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Equipos[ iDEquipo=" + iDEquipo + " ]";
    }

    public Collection<DetallePrestamo> getDetallePrestamoCollection() {
        return detallePrestamoCollection;
    }

    public void setDetallePrestamoCollection(Collection<DetallePrestamo> detallePrestamoCollection) {
        this.detallePrestamoCollection = detallePrestamoCollection;
    }
}