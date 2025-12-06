package Controlador;

import modelo.Equipos;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.logging.Logger;
import java.util.Collections;
import modelo.Usuario;
import Controlador.RolJpaController;

public class Controlador_Principal {

    private static final Logger logger = Logger.getLogger(Controlador_Principal.class.getName());
    private final RolJpaController rolController;

    // Referencia al controlador de persistencia (JPA)
    private final ActivosJpaController equiposController;
    private final UsuarioJpaController usuariosController;
    private boolean conexionExitosa = false;

    private static final String UNIDAD_PERSISTENCIA = "Audiovisuales1PU";

    public Controlador_Principal() {
        ActivosJpaController tempController = null;
        UsuarioJpaController tempUsuariosController = null;
        RolJpaController tempRolController = null;
        
        try {
            // 1. Inicializar la fábrica de entidades (Conexión)
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);
            
            // 2. Inicializar AMBOS controladores JPA
            tempController = new ActivosJpaController(emf);
            tempUsuariosController = new UsuarioJpaController(emf); 
            tempRolController = new RolJpaController(emf);
            
            this.conexionExitosa = true;
            logger.info("Conexión a la BD e inicialización de controladores exitosa.");
        } catch (PersistenceException e) {
            logger.severe("❌ ERROR: No se pudo establecer la conexión JPA: " + e.getMessage());
        }
        
        // 3. Asignar los temporales a las variables finales de la clase
        this.equiposController = tempController;
        this.usuariosController = tempUsuariosController;
        this.rolController = tempRolController;
    }

    public boolean isConexionExitosa() {
        return conexionExitosa;
    }
    
    // ===================================
    // MÉTODOS CRUD DE USUARIOS
    // ===================================

    /**
     * Guarda (crea o edita) un objeto Usuario.
     * La lógica de create/edit depende de si el objeto ya tiene un ID asignado.
     */
    public void guardarUsuario(modelo.Usuario usuario) throws Exception {
        if (!conexionExitosa || usuariosController == null) {
            throw new IllegalStateException("El controlador de usuarios no está inicializado.");
        }
        
        if (usuario.getId() == null) {
            // Si el ID es nulo, es CREACIÓN.
            usuariosController.create(usuario);
        } else {
            // Si el ID existe, es ACTUALIZACIÓN.
            usuariosController.edit(usuario);
        }
    }

    /**
     * Elimina un usuario por su ID.
     */
    public void eliminarUsuario(Integer id) throws Exception {
        if (!conexionExitosa || usuariosController == null || id == null) {
            throw new IllegalStateException("Error de conexión al intentar eliminar el usuario.");
        }
        usuariosController.destroy(id);
    }

    /**
     * Obtiene un usuario por su ID.
     */
    public Usuario obtenerUsuarioPorId(Integer id) {
        if (!conexionExitosa || usuariosController == null || id == null) {
            return null;
        }
        return usuariosController.findUsuario(id);
    }
    
    /**
     * 🚨 MÉTODO CORREGIDO: Busca un Usuario existente por su nombre de login.
     * Resuelve el error 'Not supported yet'.
     * @param nombreUsuarioLogin El nombre de login del usuario.
     * @return El objeto Usuario si existe, o null si no existe.
     */
    public Usuario obtenerUsuarioPorNombre(String nombreUsuarioLogin) {
        if (!conexionExitosa || usuariosController == null) {
            return null;
        }
        try {
            // Delega la búsqueda al controlador JPA
            return usuariosController.findUsuarioByNombreUsuario(nombreUsuarioLogin); 
        } catch (Exception e) {
            logger.severe("Error al buscar usuario por nombre: " + e.getMessage());
            return null;
        }
    }
    
    // ===================================
    // MÉTODOS DE BÚSQUEDA Y CARGA (USUARIOS)
    // ===================================
    
    public List<Usuario> obtenerTodosLosUsuarios() {
        if (!conexionExitosa || usuariosController == null) {
            return Collections.emptyList();
        }
        try {
            // Asumiendo que el UsuarioJpaController tiene el método findUsuarioEntities()
            return usuariosController.findUsuarioEntities(); 
        } catch (Exception e) {
            logger.severe("Error al obtener la lista de usuarios: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public modelo.Rol obtenerRolPorNombre(String nombre) {
        if (!conexionExitosa || rolController == null) {
            return null;
        }
        return rolController.findRolByNombre(nombre);
    }
    
    // ===================================
    // MÉTODOS CRUD (EQUIPOS)
    // ===================================

    // Método 1: Carga la lista completa de equipos (para iniciar o resetear)
    public List<Equipos> obtenerTodosLosEquipos() {
        if (!conexionExitosa || equiposController == null) {
            return Collections.emptyList();
        }

        try {
            return equiposController.findEquiposEntities();
        } catch (Exception e) {
            logger.severe("Error al obtener la lista de equipos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Delega la búsqueda de activos por criterio (Nombre, Marca, etc.) al JPA Controller.
     */
    public List<Equipos> buscarPorCriterio(String terminoBusqueda) {
        if (!conexionExitosa || equiposController == null) {
            return Collections.emptyList();
        }
        try {
            return equiposController.buscarPorCriterio(terminoBusqueda);
        } catch (Exception e) {
            logger.severe("Error al buscar equipos por criterio: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Equipos obtenerEquipoPorId(Integer id) {
        if (!conexionExitosa || equiposController == null || id == null) {
            return null;
        }
        return equiposController.findEquipos(id);
    }
    
    // Método para Guardar (Crea o Edita)
    public void guardarEquipo(Equipos equipo) throws Exception {
        if (!conexionExitosa || equiposController == null) {
            throw new IllegalStateException("Error de conexión al intentar guardar el equipo.");
        }

        if (equipo.getIDEquipo() == null) {
            equiposController.crear(equipo);
        } else {
            equiposController.edit(equipo);
        }
    }

    public void eliminarEquipo(Integer id) throws Exception {
        if (!conexionExitosa || equiposController == null || id == null) {
            throw new IllegalStateException("Error de conexión al intentar eliminar el equipo.");
        }
        equiposController.destroy(id);
    }
}