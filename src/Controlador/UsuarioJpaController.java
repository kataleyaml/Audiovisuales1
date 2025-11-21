package Controlador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import modelo.Usuario;

public class UsuarioJpaController {

    private final EntityManagerFactory emf;

    // Constructor que recibe el EntityManagerFactory
    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Busca un usuario por nombre y contraseña para la autenticación.
     */
    public Usuario buscarUsuarioPorCredenciales(String nombre, String contrasena) {
        EntityManager em = getEntityManager();
        try {
            // JPQL Query para buscar el usuario por ambos campos
            Query q = em.createQuery("SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.contraseña = :contrasena");
            q.setParameter("nombre", nombre);
            q.setParameter("contrasena", contrasena);
            
            // Intenta obtener un único resultado
            return (Usuario) q.getSingleResult();
        } catch (NoResultException e) {
            // Si no se encuentra un usuario, retorna null
            return null;
        } catch (Exception e) {
            // Manejo de otras excepciones (ej. si hay más de un resultado)
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}