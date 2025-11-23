/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author ADONYZZZ
 */


import javax.persistence.EntityManager;
import modelo.Prestamo;
import util.JPAUtil;

public class PrestamoDAO {

    private EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

    public void guardar(Prestamo p) {
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
    }

    public void actualizar(Prestamo p) {
        em.getTransaction().begin();
        em.merge(p);
        em.getTransaction().commit();
    }

    public Prestamo buscarPorId(int id) {
        return em.find(Prestamo.class, id);
    }
}
