/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author ADONYZZZ
 */


import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class GeneradorPDF {

    // Método para crear un PDF con contenido personalizado
    public static void crearPDF(String rutaArchivo, String contenido) {
        try {
            // Crear documento
            Document documento = new Document();
            
            // Indicar dónde se guardará el archivo
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivo));
            
            // Abrir el documento para escribir dentro de él
            documento.open();

            // Título
            documento.add(new Paragraph("REPORTE DEL SISTEMA"));
            documento.add(new Paragraph(" ")); // Salto de línea

            // Contenido enviado por parámetro
            documento.add(new Paragraph(contenido));

            // Cerrar
            documento.close();
            System.out.println("PDF creado exitosamente en: " + rutaArchivo);

        } catch (Exception e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        }
    }
}
