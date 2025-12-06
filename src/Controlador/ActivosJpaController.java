package Controlador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;
import modelo.Equipos; // Usar la entidad correcta
import javax.persistence.NoResultException;

public class ActivosJpaController {

    private final EntityManagerFactory emf;

    public ActivosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // ===========================
    // MÉTODO CREAR (CREATE)
    // ===========================
    public Integer crear(Equipos equipo) { 
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(equipo); 
            em.getTransaction().commit();
            return equipo.getIDEquipo(); 
            
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
    // MÉTODO LISTAR TODOS (findEquiposEntities)
    // ===========================
    public List<Equipos> findEquiposEntities() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT e FROM Equipos e");
            return q.getResultList();
        } finally {
            em.close();
        }
    }
    
    // ===========================
    // MÉTODO BUSCAR POR ID (findEquipos)
    // CORRECCIÓN CLAVE: La firma del método ahora acepta Integer, haciendo el JpaController
    // consistente con la clave primaria de la entidad Equipos.
    // ===========================
    public Equipos findEquipos(Integer id) { // <-- ¡CORREGIDO! De Long a Integer
        EntityManager em = getEntityManager();
        try {
            // El EntityManager.find() ahora recibe el tipo de clave correcto (Integer)
            return em.find(Equipos.class, id); 
        } finally {
            em.close(); 
        }
    }
    

    // ===========================
    // MÉTODO EDITAR (EDIT)
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
    public void destroy(Integer id) throws Exception { 
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
            Equipos equipo = em.find(Equipos.class, id); 
            
            if (equipo == null) {
                throw new NoResultException("El equipo con ID " + id + " no existe.");
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
    // BUSCAR POR TEXTO 
    // ===========================
    public List<Equipos> buscarPorTexto(String texto) {
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
    public List<Equipos> buscarPorCriterio(String textoBusqueda) {
    EntityManager em = getEntityManager();
    try {
        String patron = "%" + textoBusqueda.toLowerCase() + "%";
        
        // La sintaxis 'CAST(... AS string)' causa el error en MariaDB/MySQL.
        // Usamos CONCAT para convertir implícitamente el ID a String.
        Query q = em.createQuery("SELECT e FROM Equipos e WHERE " +
                "LOWER(e.nombre) LIKE :texto OR " +
                "LOWER(e.marca) LIKE :texto OR " +
                "LOWER(e.modeloSerie) LIKE :texto OR " +
                "LOWER(e.ubicacionactual) LIKE :texto OR " +
                // 🚨 CORRECCIÓN CLAVE: Usamos CONCAT para convertir ID_Equipo a String para el LIKE
                "CONCAT(e.iDEquipo, '') LIKE :texto"); 
        
        q.setParameter("texto", patron);
        return q.getResultList();
    } finally {
        em.close();
    }
    }
    
}