/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author ADONYZZZ
 */


import dao.ActivoAudiovisualDAO;
import dao.HistorialDAO;
import dao.PrestamoDAO;
import dao.UsuarioDAO;
import java.util.Date;
import modelo.ActivoAudiovisual;
import modelo.Historial;
import modelo.Prestamo;
import modelo.Usuario;

public class PrestamoController {

    private ActivoAudiovisualDAO activoDAO = new ActivoAudiovisualDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private HistorialDAO historialDAO = new HistorialDAO();

    // Prestar activo
    public String prestarActivo(int idUsuario, int idActivo) {

        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        ActivoAudiovisual a = activoDAO.buscarPorId(idActivo);

        if (u == null) return "El usuario no existe";
        if (a == null) return "El activo no existe";

        // Bloqueo: activo no disponible
        if (!a.getDisponible()) {
            return "Este activo NO está disponible.";
        }

        // Crear préstamo
        Prestamo p = new Prestamo();
        p.setUsuarioId(u);
        p.setActivoId(a);
        p.setFechaPrestamo(new Date());
        p.setEstado("PRESTADO");

        prestamoDAO.guardar(p);

        // cambiar estado del activo
        a.setDisponible(false);
        activoDAO.actualizar(a);

        // Historial
        registrarHistorial(u, a, "PRESTAMO", "El usuario tomó prestado el activo.");

        return "Préstamo registrado correctamente.";
    }

    // Devolver activo
    public String devolverActivo(int idPrestamo) {

        Prestamo p = prestamoDAO.buscarPorId(idPrestamo);
        if (p == null) return "No existe el préstamo";

        p.setEstado("DEVUELTO");
        p.setFechaDevolucion(new Date());
        prestamoDAO.actualizar(p);

        ActivoAudiovisual a = p.getActivoId();
        a.setDisponible(true);
        activoDAO.actualizar(a);

        registrarHistorial(p.getUsuarioId(), a, "DEVOLUCION", "El activo fue devuelto.");

        return "Devolución registrada.";
    }

    private void registrarHistorial(Usuario u, ActivoAudiovisual a, String accion, String desc) {

        Historial h = new Historial();
        h.setUsuarioId(u);
        h.setActivoId(a);
        h.setAccion(accion);
        h.setDescripcion(desc);
        h.setFecha(new Date());

        historialDAO.registrar(h);
    }
}
