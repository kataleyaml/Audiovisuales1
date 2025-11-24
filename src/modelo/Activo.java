package modelo;

import java.util.Date; 
// Importamos java.util.Date para manejar la fecha de adquisición.
// (En la capa DAO, tendrás que manejar la conversión a java.sql.Date si usas JDBC).

/**
 * Clase de Entidad/Modelo que representa un Activo o Equipo audiovisual 
 * en el inventario.
 */
public class Activo {
    
    // --- ATRIBUTOS (Variables de Instancia) ---
    private Long idEquipo; 
    private String nombre; // Ej: Video Beam, Micrófono, Cable HDMI
    private String marca; // Ej: Sony, Epson
    private String modeloSerie;
    private String ubicacionActual;
    private String estado; // Valores: "Disponible", "Prestado", "Dañado/Reportado"
    private Date fechaAdquisicion;
    private String observaciones;

    // --- CONSTRUCTORES ---

    /**
     * Constructor vacío. Necesario para inicializar el objeto sin valores iniciales.
     */
    public Activo() {
    }

    /**
     * Constructor completo para inicializar todos los atributos del activo.
     */
    public Activo(Long idEquipo, String nombre, String marca, String modeloSerie, String ubicacionActual, String estado, Date fechaAdquisicion, String observaciones) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.marca = marca;
        this.modeloSerie = modeloSerie;
        this.ubicacionActual = ubicacionActual;
        this.estado = estado;
        this.fechaAdquisicion = fechaAdquisicion;
        this.observaciones = observaciones;
    }

    // --- MÉTODOS ACCESORES (GETTERS Y SETTERS) ---

    // Métodos Getter
    public Long getIdEquipo() {
        return idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMarca() {
        return marca;
    }

    public String getModeloSerie() {
        return modeloSerie;
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public String getEstado() {
        return estado;
    }

    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    // Métodos Setter
    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModeloSerie(String modeloSerie) {
        this.modeloSerie = modeloSerie;
    }

    public void setUbicacionActual(String ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}