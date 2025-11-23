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
import javax.persistence.TypedQuery;
import modelo.Rol;
import util.JPAUtil;
import java.util.List;

public class RolDAO {

    private EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

    public Rol buscarPorId(int id) {
        return em.find(Rol.class, id);
    }

    public List<Rol> listar() {
        TypedQuery<Rol> q =
            em.createNamedQuery("Rol.findAll", Rol.class);
        return q.getResultList();
    }
}

