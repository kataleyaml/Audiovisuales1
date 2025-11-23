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
import modelo.Usuario;
import util.JPAUtil;
import java.util.List;

public class UsuarioDAO {

    private EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

    public Usuario buscarPorId(int id) {
        return em.find(Usuario.class, id);
    }

    public Usuario buscarPorNombre(String nombre) {
        TypedQuery<Usuario> q = em.createNamedQuery("Usuario.findByNombre", Usuario.class);
        q.setParameter("nombre", nombre);
        List<Usuario> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Usuario> listar() {
        TypedQuery<Usuario> q = em.createNamedQuery("Usuario.findAll", Usuario.class);
        return q.getResultList();
    }
}
