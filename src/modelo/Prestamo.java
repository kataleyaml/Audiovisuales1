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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entidad Prestamo (Corregida: Se eliminó la referencia a 'Estado_del_Prestamo')
 */
@Entity
@Table(name = "prestamo")
@NamedQueries({
    @NamedQuery(name = "Prestamo.findAll", query = "SELECT p FROM Prestamo p"),
    @NamedQuery(name = "Prestamo.findByIDPrestamo", query = "SELECT p FROM Prestamo p WHERE p.iDPrestamo = :iDPrestamo"),
    @NamedQuery(name = "Prestamo.findByFechaPrestamo", query = "SELECT p FROM Prestamo p WHERE p.fechaPrestamo = :fechaPrestamo"),
    @NamedQuery(name = "Prestamo.findByFechaDevolucionEstimada", query = "SELECT p FROM Prestamo p WHERE p.fechaDevolucionEstimada = :fechaDevolucionEstimada"),
    @NamedQuery(name = "Prestamo.findByProposito", query = "SELECT p FROM Prestamo p WHERE p.proposito = :proposito"),
    @NamedQuery(name = "Prestamo.findByCodigoSolicitante", query = "SELECT p FROM Prestamo p WHERE p.codigoSolicitante = :codigoSolicitante"),
    @NamedQuery(name = "Prestamo.findByNombreSolicitante", query = "SELECT p FROM Prestamo p WHERE p.nombreSolicitante = :nombreSolicitante"),
    @NamedQuery(name = "Prestamo.findByTipoUsuario", query = "SELECT p FROM Prestamo p WHERE p.tipoUsuario = :tipoUsuario"),
    @NamedQuery(name = "Prestamo.findByCorreoSolicitante", query = "SELECT p FROM Prestamo p WHERE p.correoSolicitante = :correoSolicitante")
    // Se eliminó la NamedQuery 'Prestamo.findByEstadodelPrestamo'
})
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID_Prestamo")
    private Integer iDPrestamo;
    @Basic(optional = false)
    @Column(name = "Fecha_Prestamo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPrestamo;
    @Basic(optional = false)
    @Column(name = "Fecha_Devolucion_Estimada")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevolucionEstimada;
    @Basic(optional = false)
    @Column(name = "Proposito")
    private String proposito;
    @Column(name = "Codigo_Solicitante")
    private String codigoSolicitante;
    @Basic(optional = false)
    @Column(name = "Nombre_Solicitante")
    private String nombreSolicitante;
    @Basic(optional = false)
    @Column(name = "Tipo_Usuario")
    private String tipoUsuario;
    @Column(name = "Correo_Solicitante")
    private String correoSolicitante;
    // Se ELIMINÓ la definición del campo 'estadodelPrestamo'
    
    // Relación ManyToOne con Usuario (ID_Monitor)
    @JoinColumn(name = "ID_Monitor", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Usuario iDMonitor;
    
    // Relación OneToMany con DetallePrestamo
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prestamo")
    private Collection<DetallePrestamo> detallePrestamoCollection;

    // --- Constructores ---
    public Prestamo() {
    }

    public Prestamo(Integer iDPrestamo) {
        this.iDPrestamo = iDPrestamo;
    }

    // Se eliminó 'String estadodelPrestamo' del constructor completo
    public Prestamo(Integer iDPrestamo, Date fechaPrestamo, Date fechaDevolucionEstimada, String proposito, String nombreSolicitante, String tipoUsuario) {
        this.iDPrestamo = iDPrestamo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.proposito = proposito;
        this.nombreSolicitante = nombreSolicitante;
        this.tipoUsuario = tipoUsuario;
    }

    // --- Getters y Setters ---
    public Integer getIDPrestamo() {
        return iDPrestamo;
    }

    public void setIDPrestamo(Integer iDPrestamo) {
        this.iDPrestamo = iDPrestamo;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucionEstimada() {
        return fechaDevolucionEstimada;
    }

    public void setFechaDevolucionEstimada(Date fechaDevolucionEstimada) {
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
    }

    public String getProposito() {
        return proposito;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }

    public String getCodigoSolicitante() {
        return codigoSolicitante;
    }

    public void setCodigoSolicitante(String codigoSolicitante) {
        this.codigoSolicitante = codigoSolicitante;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getCorreoSolicitante() {
        return correoSolicitante;
    }

    public void setCorreoSolicitante(String correoSolicitante) {
        this.correoSolicitante = correoSolicitante;
    }

    // Se ELIMINARON getEstadodelPrestamo y setEstadodelPrestamo
    /*
    public String getEstadodelPrestamo() {
        return estadodelPrestamo;
    }

    public void setEstadodelPrestamo(String estadodelPrestamo) {
        this.estadodelPrestamo = estadodelPrestamo;
    }
    */
    
    public Usuario getIDMonitor() {
        return iDMonitor;
    }

    public void setIDMonitor(Usuario iDMonitor) {
        this.iDMonitor = iDMonitor;
    }

    public Collection<DetallePrestamo> getDetallePrestamoCollection() {
        return detallePrestamoCollection;
    }

    public void setDetallePrestamoCollection(Collection<DetallePrestamo> detallePrestamoCollection) {
        this.detallePrestamoCollection = detallePrestamoCollection;
    }
    
    // --- HashCode, Equals y ToString ---
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDPrestamo != null ? iDPrestamo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Prestamo)) {
            return false;
        }
        Prestamo other = (Prestamo) object;
        if ((this.iDPrestamo == null && other.iDPrestamo != null) || (this.iDPrestamo != null && !this.iDPrestamo.equals(other.iDPrestamo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Prestamo[ iDPrestamo=" + iDPrestamo + " ]";
    }
}