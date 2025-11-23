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
import java.util.List;
import modelo.ActivoAudiovisual;

public class ActivoController {

    private ActivoAudiovisualDAO activoDAO = new ActivoAudiovisualDAO();

    public String crearActivo(String nombre, String descripcion, String estado, boolean disponible) {
        ActivoAudiovisual a = new ActivoAudiovisual();
        a.setNombre(nombre);
        a.setDescripcion(descripcion);
        a.setEstado(estado);
        a.setDisponible(disponible);

        activoDAO.guardar(a);
        return "Activo creado correctamente.";
    }

    public String actualizarActivo(int id, String nombre, String descripcion, String estado, boolean disponible) {

        ActivoAudiovisual a = activoDAO.buscarPorId(id);
        if (a == null) return "No existe el activo.";

        a.setNombre(nombre);
        a.setDescripcion(descripcion);
        a.setEstado(estado);
        a.setDisponible(disponible);

        activoDAO.actualizar(a);
        return "Activo actualizado con éxito.";
    }

    public ActivoAudiovisual buscarActivo(int id) {
        return activoDAO.buscarPorId(id);
    }

    public List<ActivoAudiovisual> listarActivos() {
        return activoDAO.listarTodos();
    }

    public String cambiarDisponibilidad(int id, boolean disponible) {
        ActivoAudiovisual a = activoDAO.buscarPorId(id);
        if (a == null) return "Activo no existe";

        a.setDisponible(disponible);
        activoDAO.actualizar(a);
        return "Disponibilidad actualizada.";
    }
}
