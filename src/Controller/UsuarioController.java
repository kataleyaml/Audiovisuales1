/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author ADONYZZZ
 */

import dao.UsuarioDAO;
import modelo.Usuario;
import modelo.Rol;

public class UsuarioController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public String crearUsuario(String nombre, String contraseña, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setContraseña(contraseña);
        u.setRolId(rol);

        usuarioDAO.guardar(u);
        return "Usuario creado exitosamente.";
    }

    public Usuario buscarUsuario(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public Usuario login(String nombre, String contraseña) {
        return usuarioDAO.validarCredenciales(nombre, contraseña);
    }

    public String cambiarRol(int idUsuario, Rol nuevoRol) {
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) return "El usuario no existe.";

        u.setRolId(nuevoRol);
        usuarioDAO.actualizar(u);

        return "Rol actualizado.";
    }
}
