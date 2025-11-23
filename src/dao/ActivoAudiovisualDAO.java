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
import modelo.ActivoAudiovisual;
import util.JPAUtil;
import java.util.List;

public class ActivoAudiovisualDAO {

    private EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

    public void guardar(ActivoAudiovisual activo) {
        em.getTransaction().begin();
        em.persist(activo);
        em.getTransaction().commit();
    }

    public void actualizar(ActivoAudiovisual activo) {
        em.getTransaction().begin();
        em.merge(activo);
        em.getTransaction().commit();
    }

    public ActivoAudiovisual buscarPorId(int id) {
        return em.find(ActivoAudiovisual.class, id);
    }

    public List<ActivoAudiovisual> listar() {
        TypedQuery<ActivoAudiovisual> q =
                em.createNamedQuery("ActivoAudiovisual.findAll", ActivoAudiovisual.class);
        return q.getResultList();
    }
}
