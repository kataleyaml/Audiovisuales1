/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author ADONYZZZ
 */

import dao.HistorialDAO;
import java.util.List;
import modelo.Historial;

public class HistorialController {

    private HistorialDAO historialDAO = new HistorialDAO();

    public List<Historial> listarHistorial() {
        return historialDAO.listarTodos();
    }

    public List<Historial> historialPorUsuario(int idUsuario) {
        return historialDAO.buscarPorUsuario(idUsuario);
    }

    public List<Historial> historialPorActivo(int idActivo) {
        return historialDAO.buscarPorActivo(idActivo);
    }
}
