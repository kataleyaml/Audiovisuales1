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
    // MÉTODOS DE AUTENTICACIÓN (CORREGIDO PARA USAR NAMEDQUERY)
    // =========================================================

    /**
     * Autentica un usuario usando la NamedQuery "Usuario.autenticar".
     * Recibe la entrada del login (String) y la convierte a long para la consulta.
     * @param nombreUsuarioLogin El ID de usuario (String) introducido en el login.
     * @param contrasena La contraseña introducida en el login.
     * @return El objeto Usuario si las credenciales son correctas, o null si no lo son.
     */
    public Usuario autenticarUsuario(String nombreUsuarioLogin, String contrasena) {
        EntityManager em = getEntityManager();
        try {
            // 1. Convertir la entrada del login (String) a long
            long idUsuarioLong = Long.parseLong(nombreUsuarioLogin);
            
            // 2. Ejecutar la CONSULTA NOMBRADA EXACTA definida en la entidad Usuario.java
            // NamedQuery: "Usuario.autenticar"
            Query q = em.createNamedQuery("Usuario.autenticar"); 

            // 3. Establecer parámetros con los nombres exactos de la NamedQuery: :idUsuario y :pass
            q.setParameter("idUsuario", idUsuarioLong); 
            q.setParameter("pass", contrasena);

            // 4. Obtener el resultado
            return (Usuario) q.getSingleResult();
        } catch (NumberFormatException e) {
            // Error si el usuario introduce letras en el campo de login numérico
            System.err.println("Error de formato en login: El ID de usuario no es un número válido.");
            return null;
        } catch (NoResultException e) {
            // Credenciales incorrectas, no se encontró resultado
            return null;
        } catch (Exception e) {
            System.err.println("Error fatal en autenticación: " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // =========================================================
    // MÉTODO BÚSQUEDA POR NOMBRE/LOGIN (Mantenido y Corregido para LONG)
    // Se usa JPQL directa, no NamedQuery.
    // =========================================================

    /**
     * Busca un Usuario existente por su nombre de login (asumido como tipo long).
     */
    public Usuario findUsuarioByNombreUsuario(String nombreUsuario) {
    EntityManager em = getEntityManager();
    try {
        // 1. Convertir el String de entrada a LONG para satisfacer el mapeo de la entidad Usuario.
        // Si la entidad mapea 'nombreUsuario' como Long, debemos pasar un Long.
        long idUsuarioLong = Long.parseLong(nombreUsuario); 
        
        // 2. Ejecutar la JPQL.
        // Usamos la variable long en el parámetro
        Query q = em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario");
        q.setParameter("nombreUsuario", idUsuarioLong); 
        
        return (Usuario) q.getSingleResult();
    } catch (NumberFormatException e) {
        // Esto captura si el String "nombreUsuario" no se pudo convertir a Long.
        System.err.println("Error de formato: El nombre de usuario no es un número válido (long).");
        return null; 
    } catch (NoResultException e) {
        // No se encontró el usuario
        return null;
    } catch (Exception e) {
        System.err.println("Error al buscar Usuario por nombre/código (" + nombreUsuario + "): " + e.getMessage());
        return null;
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
    
    // =========================================================
    // MÉTODOS CRUD ESTÁNDAR (create, edit, destroy, findUsuario)
    // =========================================================

    public void create(Usuario usuario) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            if (usuario.getRolId() != null) {
                usuario.setRolId(em.getReference(usuario.getRolId().getClass(), usuario.getRolId().getId()));
            }
            em.persist(usuario);
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
            usuario = em.merge(usuario);
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

    // =========================================================
    // MÉTODOS DE CONSULTA Y LISTADO (findUsuarioEntities)
    // =========================================================

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

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
}