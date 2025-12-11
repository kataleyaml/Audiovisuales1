package Controlador;

import modelo.Equipos;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.logging.Logger;
import java.util.Collections;
import modelo.Usuario;
import modelo.Rol; 
// 🚨 Importaciones de controladores necesarios (asumiendo que están en el paquete Controlador)
import Controlador.RolJpaController;
import Controlador.UsuarioJpaController;
import Controlador.PrestamosJpaController;
import Controlador.EquiposJpaController; // 🚨 AHORA DEBE EXISTIR ESTA CLASE
import modelo.Prestamo; 
import modelo.DetallePrestamo;
import java.util.Date;
import java.util.ArrayList;

public class Controlador_Principal {

    private static final Logger logger = Logger.getLogger(Controlador_Principal.class.getName());
    private final RolJpaController rolController;
    private final PrestamosJpaController prestamosController;

    private final EquiposJpaController equiposController; // 🚨 Uso de la nueva clase
    private final UsuarioJpaController usuariosController;
    private boolean conexionExitosa = false;

    private static final String UNIDAD_PERSISTENCIA = "Audiovisuales1PU";

    public Controlador_Principal() {
        EquiposJpaController tempController = null;
        UsuarioJpaController tempUsuariosController = null;
        RolJpaController tempRolController = null;
        PrestamosJpaController tempPrestamosController = null;
        
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);

            // 2. Inicializar TODOS los controladores JPA
            tempController = new EquiposJpaController(emf); // 🚨 Inicialización correcta
            tempUsuariosController = new UsuarioJpaController(emf);
            tempRolController = new RolJpaController(emf);
            tempPrestamosController = new PrestamosJpaController(emf);

            this.conexionExitosa = true;
            logger.info("Conexión a la BD e inicialización de controladores exitosa.");
        } catch (PersistenceException e) {
            logger.severe("❌ ERROR: No se pudo establecer la conexión JPA: " + e.getMessage());
        }

        this.equiposController = tempController;
        this.usuariosController = tempUsuariosController;
        this.rolController = tempRolController;
        this.prestamosController = tempPrestamosController;
    }

    public boolean isConexionExitosa() {
        return conexionExitosa;
    }
    public List<Prestamo> obtenerTodosLosPrestamosConDetalles() {
    if (prestamosController == null) {
        logger.severe("Controlador de préstamos no inicializado.");
        return new ArrayList<>();
    }
    return prestamosController.findPrestamosWithDetalles();
}

    // ===================================
    // MÉTODOS CRUD DE USUARIOS (Manteniendo su código original)
    // ===================================

    public void guardarUsuario(modelo.Usuario usuario) throws Exception {
        if (!conexionExitosa || usuariosController == null) {
            throw new IllegalStateException("El controlador de usuarios no está inicializado.");
        }
        if (usuario.getId() == null) {
            usuariosController.create(usuario);
        } else {
            usuariosController.edit(usuario);
        }
    }

    public void eliminarUsuario(Integer id) throws Exception {
        if (!conexionExitosa || usuariosController == null || id == null) {
            throw new IllegalStateException("Error de conexión al intentar eliminar el usuario.");
        }
        usuariosController.destroy(id);
    }

    public Usuario obtenerUsuarioPorId(Integer id) {
        if (!conexionExitosa || usuariosController == null || id == null) {
            return null;
        }
        return usuariosController.findUsuario(id);
    }

    public Usuario obtenerUsuarioPorNombre(String nombreUsuarioLogin) {
        if (!conexionExitosa || usuariosController == null) {
            return null;
        }
        try {
            return usuariosController.findUsuarioByNombreUsuario(nombreUsuarioLogin);
        } catch (Exception e) {
            logger.severe("Error al buscar usuario por nombre: " + e.getMessage());
            return null;
        }
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        if (!conexionExitosa || usuariosController == null) {
            return Collections.emptyList();
        }
        try {
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

    public List<Equipos> obtenerTodosLosEquipos() {
        if (!conexionExitosa || equiposController == null) {
            return Collections.emptyList();
        }
        try {
            return equiposController.findEquiposEntities(); // 🚨 Método renombrado a Equipos
        } catch (Exception e) {
            logger.severe("Error al obtener la lista de equipos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Equipos> buscarEquiposDisponibles() {
        if (!conexionExitosa || equiposController == null) {
            return Collections.emptyList();
        }
        try {
            return equiposController.findEquiposDisponibles();
        } catch (Exception e) {
            logger.severe("Error al obtener equipos disponibles: " + e.getMessage());
            return Collections.emptyList();
        }
    }

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
        // 🚨 CRUCIAL: Convertir ID a Long si el método findEquipos lo requiere (si el ID de Equipos es Long).
        // Si el ID es Integer, usa id:
        // return equiposController.findEquipos(id);
        // Si el ID es Long:
        return equiposController.findEquipos(id); 
    }

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
        // 🚨 CRUCIAL: Convertir ID a Long si el método destroy de EquiposJpaController lo requiere
        // Asumiendo que el ID de Equipos es Long en la BD:
        equiposController.destroy(id.longValue()); 
    }

    // ===================================
    // MÉTODOS DE PRESTAMOS
    // ===================================

    public boolean registrarNuevoPrestamo(Long idMonitor, String nombreSolicitante, String tipoUsuario,
                                     String correo, String proposito, Date fechaPrestamo,
                                     Date fechaDevolucionEsperada, List<Integer> idsEquipos,
                                     String codigoSolicitante) { 
    
    if (!conexionExitosa || prestamosController == null || equiposController == null || usuariosController == null) {
        logger.severe("Controladores de Prestamos, Equipos o Usuarios no inicializados.");
        return false;
    }

    try {
        // 1. CORRECCIÓN: BUSCAR EL MONITOR POR EL CÓDIGO (NOMBRE DE USUARIO)
        // Convertimos el Long (que contiene el código ej. 2467202) a String.
        String codigoMonitorStr = idMonitor.toString();
        
        // Usamos el método de la capa de control para buscar por el nombre de usuario/código.
        // Esto depende de que tengas un método como 'obtenerUsuarioPorNombre' que usa findUsuarioByNombreUsuario.
        Usuario monitor = obtenerUsuarioPorNombre(codigoMonitorStr); // <--- DEBERÍA USAR EL MÉTODO EXISTENTE
        
        // Si no tienes el método 'obtenerUsuarioPorNombre' en Controlador_Principal, usa la línea de abajo:
        // Usuario monitor = usuariosController.findUsuarioByNombreUsuario(codigoMonitorStr); 
        
        if (monitor == null) {
            // Este mensaje de error AHORA DEBE forzar el nuevo texto para confirmar la ejecución.
            logger.severe("❌ Búsqueda fallida del Monitor (Código: " + codigoMonitorStr + "). Asegúrese de que el usuario existe.");
            return false;
        }

        // 2. Crear el objeto Prestamo principal (Cabecera)
        Prestamo nuevoPrestamo = new Prestamo();
        
        nuevoPrestamo.setIDMonitor(monitor); // Asignamos el objeto REAL
        
        nuevoPrestamo.setNombreSolicitante(nombreSolicitante);
        nuevoPrestamo.setTipoUsuario(tipoUsuario);
        nuevoPrestamo.setProposito(proposito);
        nuevoPrestamo.setCorreoSolicitante(correo);
        nuevoPrestamo.setFechaPrestamo(fechaPrestamo);
        nuevoPrestamo.setFechaDevolucionEstimada(fechaDevolucionEsperada);
        nuevoPrestamo.setCodigoSolicitante(codigoSolicitante); 

        // 3. Crear el DetallePrestamo y asociar los equipos
        List<DetallePrestamo> detalles = new ArrayList<>();
        
        for (Integer idEquipo : idsEquipos) {
            Equipos equipoReal = equiposController.findEquipos(idEquipo); 
            
            if (equipoReal != null) {
                DetallePrestamo detalle = new DetallePrestamo();
                
                // Proxy para evitar el rastreo de JPA
                Equipos equipoProxy = new Equipos(idEquipo); 
                
                detalle.setEquipos(equipoProxy); 
                detalle.setPrestamo(nuevoPrestamo);
                
                // Copiamos la metadata
                detalle.setNombreEquipo(equipoReal.getNombre());     
                detalle.setMarca(equipoReal.getMarca());
                detalle.setModeloSerie(equipoReal.getModeloSerie());
                
                detalles.add(detalle);
            } else {
                 logger.warning("Equipo no encontrado para la creación del detalle: ID " + idEquipo);
            }
        }
        
        if (detalles.isEmpty()) {
            logger.warning("No se encontraron equipos válidos para el préstamo.");
            return false;
        }

        // 5. Guardar el Prestamo y los Detalles. 
        prestamosController.crearPrestamoConDetallesYActualizarEquipos(nuevoPrestamo, detalles, idsEquipos);

        return true;

    } catch (Exception e) {
        logger.severe("Error al registrar nuevo préstamo: " + e.getMessage());
        return false;
    } 
}
}