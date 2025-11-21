package Controlador;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import modelo.Usuario;

public class controlador_login {

    // 1. Instancia del controlador JPA (ya no es 'final' y se inicializa a null)
    // Se asume que UsuarioJpaController ya fue creado en este paquete
    private UsuarioJpaController usuarioJpaController = null;
    
    // 2. Nueva bandera para el estado de la conexión, inicializada a true
    private boolean conexionExitosa = true; 
    
    // Nombre de tu unidad de persistencia (Verificado en persistence.xml: Audiovisaules1PU)
    private static final String UNIDAD_PERSISTENCIA = "Audiovisaules1PU";

    /**
     * Constructor: Inicializa la fábrica de entidades (EMF) y el controlador JPA.
     * Añade manejo de excepciones de conexión.
     */
    public controlador_login() {
        try {
            // Intenta crear la conexión a la BD
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);
            this.usuarioJpaController = new UsuarioJpaController(emf);
            this.conexionExitosa = true; // Conexión exitosa
        } catch (PersistenceException e) {
            // Si la conexión falla (BD no activa o inaccesible)
            System.err.println("❌ ERROR DE CONEXIÓN A LA BASE DE DATOS: " + e.getMessage());
            this.usuarioJpaController = null; // Se mantiene o se asigna null
            this.conexionExitosa = false; // Conexión fallida
        }
    }

    /**
     * Devuelve el estado de la conexión a la BD.
     */
    public boolean isConexionExitosa() {
        return conexionExitosa;
    }

    /**
     * Intenta autenticar a un usuario usando nombre y contraseña.
     * Verifica primero el estado de la conexión.
     * @param nombre El nombre de usuario ingresado.
     * @param contrasena La contraseña ingresada.
     * @return El objeto Usuario si la autenticación es exitosa, o null si falla.
     */
    public Usuario autenticarUsuario(String nombre, String contrasena) {
        if (!conexionExitosa || usuarioJpaController == null) {
            // Si la conexión falló en el constructor, no intentes buscar.
            return null; 
        }
        
        // La lógica de búsqueda real está en UsuarioJpaController
        return usuarioJpaController.buscarUsuarioPorCredenciales(nombre, contrasena);
    }
}