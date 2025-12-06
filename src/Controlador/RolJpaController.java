package Controlador;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import modelo.Rol;

/**
 * Controlador JPA para la entidad Rol.
 * Proporciona métodos CRUD y métodos de búsqueda específicos.
 * @author Jose
 */
public class RolJpaController implements Serializable {

    private final EntityManagerFactory emf;

    // Constructor que recibe el EntityManagerFactory
    public RolJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // =========================================================
    // MÉTODOS CRUD ESTÁNDAR (Opcionales, pero buena práctica)
    // =========================================================

    public void create(Rol rol) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(rol);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // (Se omiten edit() y destroy() por brevedad, pero deberían estar aquí)

    // =========================================================
    // MÉTODO CLAVE: Búsqueda por Nombre
    // =========================================================

    /**
     * Busca una entidad Rol por su nombre (campo único o clave de negocio).
     * @param nombre El nombre del rol a buscar.
     * @return El objeto Rol si se encuentra, o null si no existe.
     */
    public Rol findRolByNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            // Usamos una NamedQuery definida en la entidad Rol si existe, o JPQL directa.
            // Asumo que tu entidad Rol tiene la NamedQuery: @NamedQuery(name = "Rol.findByNombre", query = "SELECT r FROM Rol r WHERE r.nombre = :nombre")
            Query q = em.createNamedQuery("Rol.findByNombre"); 
            q.setParameter("nombre", nombre);
            
            // Intenta obtener un único resultado
            return (Rol) q.getSingleResult();
        } catch (NoResultException e) {
            // Si la consulta no devuelve resultados, retorna null
            return null;
        } catch (Exception e) {
            // Manejo de otras excepciones (e.g., más de un resultado)
            System.err.println("Error al buscar Rol por nombre: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    // =========================================================
    // MÉTODO: Obtener todos los Roles (Útil para llenar el ComboBox)
    // =========================================================
    
    /**
     * Devuelve todos los objetos Rol.
     * @return Una lista de objetos Rol.
     */
    public List<Rol> findRolEntities() {
        return findRolEntities(true, -1, -1);
    }

    public List<Rol> findRolEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Rol.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Busca un Rol por su clave primaria (ID).
     * @param id La clave primaria del Rol.
     * @return El objeto Rol encontrado, o null.
     */
    public Rol findRol(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Rol.class, id);
        } finally {
            em.close();
        }
    }
}