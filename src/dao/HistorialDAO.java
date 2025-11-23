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
import modelo.Historial;
import util.JPAUtil;

public class HistorialDAO {

    private EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

    public void registrar(Historial h) {
        em.getTransaction().begin();
        em.persist(h);
        em.getTransaction().commit();
    }
}

