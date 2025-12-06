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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "usuario")
@NamedQueries({
    // Usamos 'nombreUsuario' para el login, que es de tipo 'long'
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u WHERE u.id = :id"),
    // Query personalizado para la autenticación
    @NamedQuery(name = "Usuario.autenticar", 
                query = "SELECT u FROM Usuario u WHERE u.nombreUsuario = :idUsuario AND u.contrasena = :pass")
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // --- Mapeo de campos de Login ---
    @Basic(optional = false)
    @Column(name = "nombre_usuario")
    private long nombreUsuario; // Campo de ID de login (numérico)
    
    @Basic(optional = false)
    @Column(name = "contrasena")
    private String contrasena; // Contraseña (String)
    
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    // --------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Rol rolId;

    public Usuario() {
    }

    public Usuario(Integer id) {
        this.id = id;
    }

    // Constructor que usa los campos correctos
    public Usuario(long nombreUsuario, String contrasena, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
    }

    // --- Getters y Setters de los campos CORREGIDOS ---
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(long nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    // --- Getters y Setters de Colecciones y Rol ---
    
    public Rol getRolId() {
        return rolId;
    }

    public void setRolId(Rol rolId) {
        this.rolId = rolId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Usuario[ id=" + id + ", nombreUsuario=" + nombreUsuario + " ]";
    }

    public void setNombreUsuario(String nombreUsuarioLogin) {
    // Si el ID de usuario (login) se maneja internamente como long, 
    // debes convertir la entrada String antes de asignarla.
    try {
        this.nombreUsuario = Long.parseLong(nombreUsuarioLogin);
    } catch (NumberFormatException e) {
        // Manejar o registrar el error si el usuario introduce texto no numérico
        System.err.println("Advertencia: Se intentó asignar un login no numérico.");
        // Opcional: Asignar un valor por defecto o lanzar una excepción para la vista
    }
    }
}