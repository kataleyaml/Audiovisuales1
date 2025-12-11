package Controlador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;
import java.util.Collections;
import modelo.Equipos;
import javax.persistence.EntityNotFoundException;

public class EquiposJpaController {

    private final EntityManagerFactory emf;

    public EquiposJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // ===========================
    // MÉTODO GUARDAR (CREATE)
    // ===========================
    public void crear(Equipos equipo) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(equipo);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar el equipo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ===========================
    // MÉTODO BUSCAR POR ID (findEquipos)
    // Requerido por Controlador_Principal.java
    // ===========================
    // Asumimos que el ID es Integer, si es Long en la BD, se debe ajustar
    public Equipos findEquipos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipos.class, id);
        } finally {
            em.close();
        }
    }

    // ===========================
    // MÉTODO EDITAR (UPDATE)
    // ===========================
    public void edit(Equipos equipo) throws Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(equipo);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // ===========================
    // MÉTODO ELIMINAR (DESTROY)
    // ===========================
    // Lo definimos con Long, asumiendo que el ID de la BD es de este tipo.
    public void destroy(Long id) throws Exception {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            Equipos equipo = em.find(Equipos.class, id);
            
            if (equipo == null) {
                throw new EntityNotFoundException("El equipo con ID " + id + " no existe.");
            }
            
            em.remove(equipo);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    // ===========================
    // LISTAR TODOS (findEquiposEntities)
    // Requerido por Controlador_Principal.java
    // ===========================
    public List<Equipos> findEquiposEntities() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Equipos e");
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    // ===========================
    // BUSCAR POR CRITERIO (buscarPorCriterio)
    // Requerido por Controlador_Principal.java
    // ===========================
    public List<Equipos> buscarPorCriterio(String texto) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Equipos e WHERE "
                                     + "LOWER(e.nombre) LIKE :txt OR "
                                     + "LOWER(e.marca) LIKE :txt OR "
                                     + "LOWER(e.modeloSerie) LIKE :txt");

            q.setParameter("txt", "%" + texto.toLowerCase() + "%");

            return q.getResultList();
        } finally {
            em.close();
        }
    }
    
    // ===========================
    // BUSCAR DISPONIBLES (findEquiposDisponibles)
    // Requerido por Controlador_Principal.java
    // ===========================
    public List<Equipos> findEquiposDisponibles() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Equipos e WHERE e.estado = 'Disponible'");
            return q.getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}