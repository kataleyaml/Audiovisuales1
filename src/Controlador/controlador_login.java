package Controlador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import modelo.Usuario;

// Asumo que tienes una clase UsuarioJpaController en el paquete Controlador o en otro paquete.
// Si está en otro paquete, asegúrate de importarla correctamente.
// import Controlador.UsuarioJpaController; 

public class controlador_login {

    private EntityManagerFactory emf = null;
    private boolean conexionExitosa = true;
    
    // Nombre de la unidad de persistencia (Debe coincidir con persistence.xml)
    private static final String UNIDAD_PERSISTENCIA = "Audiovisuales1PU";
    
    /**
     * Constructor: Inicializa la fábrica de entidades (EMF) y maneja excepciones.
     */
    public controlador_login() {
        try {
            // Intenta crear la conexión a la BD
            this.emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA);
            this.conexionExitosa = true; 
        } catch (PersistenceException e) {
            // Manejo de la excepción de conexión
            System.err.println("❌ ERROR DE CONEXIÓN A LA BASE DE DATOS: " + e.getMessage());
            this.conexionExitosa = false;
        }
    }

    /**
     * Devuelve el estado de la conexión a la BD.
     */
    public boolean isConexionExitosa() {
        return conexionExitosa;
    }

    /**
     * Autentica a un usuario usando ID (numérico) y contraseña (String).
     * Utilizamos el NamedQuery "Usuario.autenticar" que definimos en la entidad Usuario.
     * @param idUsuario El ID/nombre de usuario ingresado (numérico, long).
     * @param contrasena La contraseña ingresada.
     * @return El objeto Usuario si la autenticación es exitosa, o null si falla.
     */
    public Usuario autenticarUsuario(long idUsuario, String contrasena) {
        if (!conexionExitosa || emf == null) {
            return null; // No intentes buscar si la conexión falló
        }
        
        EntityManager em = emf.createEntityManager();
        Usuario usuario = null;
        
        try {
            // Usamos el NamedQuery "Usuario.autenticar"
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.autenticar", Usuario.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("pass", contrasena); // El parámetro se llama :pass en el NamedQuery
            
            usuario = query.getSingleResult();
            
        } catch (NoResultException e) {
            usuario = null; // No se encontró usuario con esas credenciales
        } catch (Exception e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
            usuario = null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return usuario;
    }
    
    // Método obsoleto: Eliminado o mantenido como marcador
    // public Usuario autenticarUsuario(String nombre, String contrasena) {
    //     // Ya no es necesario si solo usamos el método con 'long idUsuario'
    //     return null; 
    // }
}