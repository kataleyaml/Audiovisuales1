package vista;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Color_Celda_intercalada extends DefaultTableCellRenderer 
{
    // Colores para las filas pares (0, 2, 4, ...)
    private static final Color COLOR_PAR_FONDO = Color.RED;
    private static final Color COLOR_PAR_TEXTO = Color.WHITE;
    
    // Colores para las filas impares (1, 3, 5, ...)
    private static final Color COLOR_IMPAR_FONDO = Color.WHITE; // Usamos blanco explícito
    private static final Color COLOR_IMPAR_TEXTO = Color.BLACK;

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, 
            boolean isSelected, 
            boolean hasFocus, 
            int row, 
            int column)
    {
        // 1. Llama al método padre para el renderizado base y manejo de selección.
        // Esto permite que el componente renderer gestione el color de selección.
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // 2. Si la fila está seleccionada, NO INTERVENIMOS. 
        // Dejamos que el color de selección de la JTable se muestre.
        if (isSelected) {
            // No establecemos ningún color para que Swing use el color de selección por defecto.
            return this;
        }

        // 3. Aplicar colores intercalados
        if (row % 2 == 0) // Filas Pares (0, 2, 4, ...)
        {
            this.setBackground(COLOR_PAR_FONDO);
            this.setForeground(COLOR_PAR_TEXTO);
        }
        else // Filas Impares (1, 3, 5, ...)
        {
            // 🚨 AJUSTE CLAVE: Forzar el color blanco y el color negro del texto.
            this.setBackground(COLOR_IMPAR_FONDO); // Fondo Blanco
            this.setForeground(COLOR_IMPAR_TEXTO); // Texto Negro
        }
        
        return this;
    }
    
    // El método evaluarPar ya no es necesario, usamos la lógica de módulo directamente en el getTableCellRendererComponent
}