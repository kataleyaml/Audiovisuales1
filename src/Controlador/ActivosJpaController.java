/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author ADONYZZZ
 */


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import java.util.List;


public class ActivosJpaController {

    private final EntityManagerFactory emf;

    public ActivosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // ===========================
    //  MÉTODO GUARDAR (CREATE)
    // ===========================
    public void crear(Activos activo) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(activo);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===========================
    //  MÉTODO EDITAR (UPDATE)
    // ===========================
    public void editar(Activos activo) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(activo);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===========================
    //  MÉTODO ELIMINAR
    // ===========================
    public void eliminar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Activos a = em.find(Activos.class, id);
            if (a != null) {
                em.remove(a);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // ===========================
    //  BUSCAR POR ID
    // ===========================
    public Activos buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Activos.class, id);
        } finally {
            em.close();
        }
    }

    // ===========================
    //  LISTAR TODOS
    // ===========================
    public List<Activos> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT a FROM Activos a");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    // ===========================
    //  BUSCAR POR TEXTO (NOMBRE, MARCA, MODELO)
    // ===========================
    public List<Activos> buscarPorTexto(String texto) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("SELECT a FROM Activos a WHERE "
                    + "LOWER(a.nombre) LIKE :txt OR "
                    + "LOWER(a.marca) LIKE :txt OR "
                    + "LOWER(a.modelo) LIKE :txt");

            q.setParameter("txt", "%" + texto.toLowerCase() + "%");

            return q.getResultList();
        } finally {
            em.close();
        }
    }

    private static class Activos {

        public Activos() {
        }
    }
}
