/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "activo_audiovisual")
@NamedQueries({
    @NamedQuery(name = "ActivoAudiovisual.findAll", query = "SELECT a FROM ActivoAudiovisual a"),
    @NamedQuery(name = "ActivoAudiovisual.findById", query = "SELECT a FROM ActivoAudiovisual a WHERE a.id = :id"),
    @NamedQuery(name = "ActivoAudiovisual.findByNombre", query = "SELECT a FROM ActivoAudiovisual a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "ActivoAudiovisual.findByEstado", query = "SELECT a FROM ActivoAudiovisual a WHERE a.estado = :estado"),
    @NamedQuery(name = "ActivoAudiovisual.findByDisponible", query = "SELECT a FROM ActivoAudiovisual a WHERE a.disponible = :disponible")})
public class ActivoAudiovisual implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Lob
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @Column(name = "disponible")
    private boolean disponible;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activoId")
    private Collection<Prestamo> prestamoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activoId")
    private Collection<Historial> historialCollection;

    public ActivoAudiovisual() {
    }

    public ActivoAudiovisual(Integer id) {
        this.id = id;
    }

    public ActivoAudiovisual(Integer id, String nombre, String estado, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.disponible = disponible;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Collection<Prestamo> getPrestamoCollection() {
        return prestamoCollection;
    }

    public void setPrestamoCollection(Collection<Prestamo> prestamoCollection) {
        this.prestamoCollection = prestamoCollection;
    }

    public Collection<Historial> getHistorialCollection() {
        return historialCollection;
    }

    public void setHistorialCollection(Collection<Historial> historialCollection) {
        this.historialCollection = historialCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActivoAudiovisual)) {
            return false;
        }
        ActivoAudiovisual other = (ActivoAudiovisual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.ActivoAudiovisual[ id=" + id + " ]";
    }
    
}
