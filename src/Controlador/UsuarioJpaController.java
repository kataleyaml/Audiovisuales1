package Controlador;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import modelo.Usuario;

public class UsuarioJpaController implements Serializable {

    private final EntityManagerFactory emf;

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // =========================================================
    // 🚨 CORRECCIÓN CLAVE: BÚSQUEDA POR NOMBRE/LOGIN
    // (Maneja la conversión de String a long)
    // =========================================================

    /**
     * Busca un Usuario existente por su nombre de login (asumido como tipo long).
     */
    public Usuario findUsuarioByNombreUsuario(String nombreUsuario) {
        EntityManager em = getEntityManager();
        try {
            // 1. Intentar convertir el String de entrada a LONG
            long idUsuarioLong = Long.parseLong(nombreUsuario); 

            // 2. Ejecutar la JPQL con el valor LONG
            Query q = em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario");
            q.setParameter("nombreUsuario", idUsuarioLong); 
            
            return (Usuario) q.getSingleResult();
        } catch (NumberFormatException e) {
            // Si el String no es un número válido (ej: "Juan"), atrapa el error
            System.err.println("Error de formato: El nombre de usuario no es un número válido (long).");
            return null; 
        } catch (NoResultException e) {
            // Si no lo encuentra, retorna null
            return null;
        } catch (Exception e) {
            System.err.println("Error al buscar Usuario por nombre: " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    // =========================================================
    // 🚨 CORRECCIÓN CLAVE: MÉTODOS CRUD ESTÁNDAR (Elimina "Not supported yet")
    // =========================================================

    public void create(Usuario usuario) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            // Manejo de la relación con Rol
            if (usuario.getRolId() != null) {
                usuario.setRolId(em.getReference(usuario.getRolId().getClass(), usuario.getRolId().getId()));
            }
            em.persist(usuario); // 🚨 IMPLEMENTACIÓN CORRECTA
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            usuario = em.merge(usuario); // 🚨 IMPLEMENTACIÓN CORRECTA
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            
            if (usuario == null) {
                throw new javax.persistence.EntityNotFoundException("El usuario con ID " + id + " no existe.");
            }
            
            em.remove(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    // =========================================================
    // MÉTODOS DE CONSULTA Y LISTADO (Mantenidos)
    // =========================================================

    // Método auxiliar para obtener la lista de todas las entidades
    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    // Método que implementa la lógica de la Criteria Query
    public List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            
            Query q = em.createQuery(cq);
            
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    // Método para buscar por ID
    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    // Método de autenticación
    public Usuario buscarUsuarioPorCredenciales(long idUsuario, String contrasena) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nombreUsuario = :idUsuario AND u.contrasena = :pass");
            
            q.setParameter("idUsuario", idUsuario);
            q.setParameter("pass", contrasena);
            
            return (Usuario) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}