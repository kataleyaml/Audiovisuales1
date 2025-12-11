/*o
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;
import Controlador.EquiposJpaController;
import Controlador.Controlador_Principal;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import modelo.Activo;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Date;
import javax.persistence.Persistence;
import modelo.Equipos;
import java.util.Date; 
import javax.swing.SpinnerDateModel;
import modelo.Usuario;
import modelo.Prestamo;         // <--- AGREGAR ESTA LÍNEA
import modelo.DetallePrestamo;
/**
 *
 * @author Jose
 */
public class Principal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Principal.class.getName());
    

    private int indicePestanaAnterior;
    // Atributo global para manejar la lista temporal de equipos a prestar
    private Controlador_Principal controladorPrincipal;
    private DefaultTableModel modeloDetallePrestamo;

    // Atributo global para almacenar los objetos Equipos disponibles
    private List<modelo.Equipos> equiposDisponibles;
    private List<Integer> obtenerIDsEquiposDeDetalle() {
        List<Integer> idsEquipos = new ArrayList<>();
        DefaultTableModel modelo = (DefaultTableModel) jTableDetalle_Prestamo.getModel();
        
        // El ID del equipo es la columna 1, según la configuración de configurarTablaDetallePrestamo
        final int COLUMNA_ID_EQUIPO = 1; 

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                // Obtener el valor de la columna 1 (ID activo)
                Object idObj = modelo.getValueAt(i, COLUMNA_ID_EQUIPO);
                if (idObj != null) {
                    // Convertir el ID a Integer (puede ser Long si así lo mapeaste, 
                    // pero Integer es más común para PK en Java si es IDENTITY/auto-incremental)
                    idsEquipos.add(Integer.parseInt(idObj.toString()));
                }
            } catch (NumberFormatException e) {
                logger.severe("Error al convertir ID de equipo a número en la fila " + i + ": " + e.getMessage());
                // Puedes optar por saltar o lanzar una excepción aquí.
            }
        }
        return idsEquipos;
    }
    private void limpiarCampos() {
    // 🚩 PASO CLAVE: Deseleccionar cualquier fila en la tabla
    Tabla_activos.clearSelection(); 

    // 1. Limpiar campos de texto
    jTextFieldID.setText("");
    jTextFieldNombre.setText("");
    // ... otros campos ...
    
    // 4. RESTABLECER EL ESTADO DE LOS BOTONES
    // Configuración para el estado que quieres: HABILITAR AMBOS
    Boton_eliminar.setEnabled(true); // <-- ¡CAMBIADO a TRUE!
    Boton_guardar.setEnabled(true);
    
    // Enfocar el cursor
    jTextFieldNombre.requestFocus(); 
    }
    private void llenarTablaConLista(List<modelo.Equipos> listaEquipos) {
    // Obtener el modelo de tabla actual para limpiarlo
    DefaultTableModel modelo = (DefaultTableModel) Tabla_activos.getModel();
    modelo.setRowCount(0);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Llenar el modelo de la tabla con los datos
    for (modelo.Equipos equipo : listaEquipos) {
        String fechaStr = (equipo.getFechaadquisicion() != null)
                             ? sdf.format(equipo.getFechaadquisicion())
                             : "N/A";

        Object[] fila = new Object[]{
            equipo.getIDEquipo(),
            equipo.getNombre(),
            equipo.getMarca(),
            equipo.getModeloSerie(),
            equipo.getUbicacionactual(),
            equipo.getEstado(),
            fechaStr,
            equipo.getObservaciones()
        };
        modelo.addRow(fila);
    }
 }
    private void cargarDetallePrestamos() {
    // Asegurarse de que el modelo exista y que el controlador principal esté disponible
    if (modeloDetallePrestamo == null || controladorPrincipal == null) {
        System.err.println("Error: Modelo de tabla o Controlador Principal no inicializado.");
        return;
    }
    
    // 1. Limpiar la tabla antes de cargar nuevos datos
    modeloDetallePrestamo.setRowCount(0);
    
    // 2. Obtener la lista de Préstamos (con sus detalles ya cargados)
    List<Prestamo> listaPrestamos = controladorPrincipal.obtenerTodosLosPrestamosConDetalles();
    
    if (listaPrestamos.isEmpty()) {
        System.out.println("No se encontraron préstamos en la base de datos.");
        return;
    }
    
    // 3. Iterar sobre cada Prestamo y luego sobre sus DetallePrestamo
    for (Prestamo prestamo : listaPrestamos) {
        // La colección DetallePrestamoCollection se trae gracias al JOIN FETCH
        if (prestamo.getDetallePrestamoCollection() != null) {
            
            for (DetallePrestamo detalle : prestamo.getDetallePrestamoCollection()) {
                
                // Mapeo de la fila según las 13 columnas que definiste en configurarTablaDetallePrestamo()
                Object[] fila = new Object[]{
                    // 1. Datos de DetallePrestamo (PK e Metadata del Equipo)
                    prestamo.getIDPrestamo(), // ID Préstamo
                    detalle.getDetallePrestamoPK().getID_Activo(), // ID activo
                    detalle.getNombreEquipo(), // Nombre Equipo
                    detalle.getMarca(), // Marca
                    detalle.getModeloSerie(), // Modelo/serie
                    
                    // 2. Datos del Prestamo (Cabecera)
                    prestamo.getIDMonitor().getNombreUsuario(), // ID Administrador/Monitor (usando el código/nombre de usuario)
                    prestamo.getFechaPrestamo(), // Fecha de Préstamo
                    prestamo.getFechaDevolucionEstimada(), // Fecha de Devolución Estimada
                    prestamo.getProposito(), // Propósito
                    prestamo.getCodigoSolicitante(), // Código del solicitante
                    prestamo.getNombreSolicitante(), // Nombre del Solicitante
                    prestamo.getTipoUsuario(), // Tipo de Usuario
                    prestamo.getCorreoSolicitante() // Correo Solicitante
                };
                modeloDetallePrestamo.addRow(fila);
            }
        }
    }
}
    private void cargarActivosEnTabla(List<modelo.Equipos> listaEquipos) {
    if (listaEquipos == null) {
        listaEquipos = new java.util.ArrayList<>();
    }
    
    // Llama al método auxiliar con la lista proporcionada.
    llenarTablaConLista(listaEquipos);
    }
    private void llenarTablaUsuariosConLista(List<Usuario> listaUsuarios) {
    // 1. Definir los encabezados de la tabla
    DefaultTableModel modelo = new DefaultTableModel(
        new Object[]{"ID", "Nombre Usuario", "Contraseña", "Nombre Completo", "Rol"}, // 🚨 Tus 5 columnas
        0 // 0 filas iniciales
    );
    
    // 2. Asignar el modelo a tu tabla de usuarios
    jTableUsuarios.setModel(modelo); // Asumo que se llama jTableUsuarios

    // 3. Llenar el modelo con los datos
    for (Usuario usuario : listaUsuarios) {
        
        // 🚩 LÓGICA CLAVE: Obtener el nombre del Rol a través del objeto Rol
        String nombreRol = usuario.getRolId() != null ? usuario.getRolId().getNombre() : "Sin Rol"; 

        Object[] fila = new Object[]{
            usuario.getId(),
            usuario.getNombreUsuario(),
            usuario.getContrasena(),
            usuario.getNombreCompleto(),
            nombreRol // <-- El dato del rol viene de la tabla 'rol'
        };
        modelo.addRow(fila);
    }
    }
    private void cargarEquiposDisponibles() {
    try {
        // Asumiendo que existe un método en el controlador para buscar equipos disponibles
        equiposDisponibles = controladorPrincipal.buscarEquiposDisponibles();
        
        DefaultTableModel modelo = (DefaultTableModel) jTablePrestamo.getModel();
        modelo.setRowCount(0);

        for (modelo.Equipos equipo : equiposDisponibles) {
            Object[] fila = new Object[]{
                equipo.getIDEquipo(),
                equipo.getNombre(),
                equipo.getMarca(),
                equipo.getModeloSerie()
            };
            modelo.addRow(fila);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar equipos disponibles.", "Error DB", JOptionPane.ERROR_MESSAGE);
    }
}
    /**
     * Creates new form Gestion_Activos
     */
    public Principal(String nombreUsuario) {
    // 🚩 ELIMINADA: this.controladorPrincipal = controladorPrincipal; 

    initComponents();
    configurarTablaDetallePrestamo(); // Necesario para inicializar modeloDetallePrestamo

    // Inicialización de renderers...
    jTableUsuarios.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    Tabla_activos.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTablePrestamo.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTableDetalle_Prestamo.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTable6.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTable5.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTable4.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTable7.setDefaultRenderer(Object.class, new Color_Celda_intercalada());
    jTable8.setDefaultRenderer(Object.class, new Color_Celda_intercalada());

    Etiqueta_de_Bienvenida.setText("Hola " + nombreUsuario + ", Bienvenido al sistema de gestión de recursos audiovisuales.");

    try {
        // 🚩 CORRECCIÓN CRÍTICA: Inicializa la variable 'controladorPrincipal'
        controladorPrincipal = new Controlador_Principal(); 
        
        // 🚩 Si 'controlador' es una variable de clase y la usas en otros métodos,
        // también debes asignarla para que apunte al mismo objeto:
        

        if (controladorPrincipal.isConexionExitosa()) { // Usa controladorPrincipal
            logger.info("Controlador de Equipos inicializado con éxito.");
            
            cargarActivosEnTabla(controladorPrincipal.obtenerTodosLosEquipos()); // Pasa la lista
            cargarTablaUsuarios(); 
            cargarEquiposDisponibles(); // Ahora usa controladorPrincipal internamente
            
            // 🚩 DEPURACIÓN AÑADIDA: Llama al método de carga de la tabla de Préstamos
            System.out.println("--- INICIANDO CARGA DE DETALLES DE PRÉSTAMOS ---");
            cargarDetallePrestamos();
            System.out.println("--- FINALIZADA LA CARGA DE DETALLES DE PRÉSTAMOS ---");
            
        } else {
            JOptionPane.showMessageDialog(this,
                "Error grave: El sistema no pudo establecer la conexión a la base de datos.",
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        logger.severe("Error inesperado en Principal: " + e.getMessage());
    }
}
    public Principal() {
    initComponents();
    
    // 🚩 CORRECCIÓN: Usar un texto estático o vacío
    Etiqueta_de_Bienvenida.setText("Hola Usuario, Bienvenido al sistema de gestión de recursos audiovisuales.");
    
    try {
        // Inicializa la conexión y el controlador
        controladorPrincipal = new Controlador_Principal(); 

        if (controladorPrincipal.isConexionExitosa()) {
             logger.info("Controlador de Equipos inicializado con éxito.");
             cargarActivosEnTabla(); // Llamar solo si la conexión fue bien
             cargarTablaUsuarios();
        } else {
             JOptionPane.showMessageDialog(this, 
                 "Error grave: El sistema no pudo establecer la conexión a la base de datos.", 
                 "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        logger.severe("Error inesperado en Principal: " + e.getMessage());
    }
    }
    private void configurarTablaDetallePrestamo() {
    // Definición completa de las 13 columnas, incluyendo los placeholders del préstamo.
    modeloDetallePrestamo = new DefaultTableModel(
        new Object[]{
            "ID Préstamo", 
            "ID activo", 
            "Nombre Equipo", 
            "Marca", 
            "Modelo/serie",
            "ID Administrador/Monitor", 
            "Fecha de Préstamo", 
            "Fecha de Devolución Estimada",
            "Propósito",
            "Codigo del solicitante",
            "Nombre del Solicitante",
            "Tipo de Usuario",
            "Correo Solicitante"
        }, 
        0
    );
    jTableDetalle_Prestamo.setModel(modeloDetallePrestamo);
}

    private void configurarSpinnersFechaHora() {
    Date now = new Date();
    // 1. Configuración del Spinner de Fecha y Hora de Préstamo
    jSpinnerFecha_Prestamo.setModel(new SpinnerDateModel(now, null, null, java.util.Calendar.MINUTE));
    // Formato: Año/Mes/Día Hora:Minuto
    jSpinnerFecha_Prestamo.setEditor(new javax.swing.JSpinner.DateEditor(jSpinnerFecha_Prestamo, "yyyy/MM/dd HH:mm"));

    // 2. Configuración del Spinner de Devolución Estimada (Ejemplo: Mañana a la misma hora)
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(now);
    cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
    
    jSpinnerFecha_devolucion.setModel(new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.MINUTE));
    jSpinnerFecha_devolucion.setEditor(new javax.swing.JSpinner.DateEditor(jSpinnerFecha_devolucion, "yyyy/MM/dd HH:mm"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        Menu = new javax.swing.JPanel();
        gestion_usuarios = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jButtonGestion_Usuarios = new javax.swing.JButton();
        Gestion_equipos = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButtonGestion_Equipos = new javax.swing.JButton();
        Prestamo = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButtonRegistro_prestamos = new javax.swing.JButton();
        Devoluciones = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jButtonRegistro_devolucion = new javax.swing.JButton();
        Reportes_analisis = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButtonReporte_analisis = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jTextFieldBuscar = new javax.swing.JTextField();
        jLabelBuscar = new javax.swing.JLabel();
        jButtonBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabla_activos = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldID = new javax.swing.JTextField();
        jTextFieldNombre = new javax.swing.JTextField();
        jTextFieldMarca = new javax.swing.JTextField();
        jTextFieldModelo = new javax.swing.JTextField();
        jComboBoxEstado = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaObservaciones = new javax.swing.JTextArea();
        Boton_nuevo = new javax.swing.JButton();
        Boton_guardar = new javax.swing.JButton();
        Boton_eliminar = new javax.swing.JButton();
        Boton_cancelar = new javax.swing.JButton();
        jSpinnerFecha_de_adquisicion = new javax.swing.JSpinner();
        Ubicacion_actual = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Id_cajadetexto = new javax.swing.JTextField();
        caja_texto_nombre = new javax.swing.JTextField();
        caja_texto_nombre_completo = new javax.swing.JTextField();
        caja_texto_contraseña = new javax.swing.JPasswordField();
        combo_box_Rol = new javax.swing.JComboBox<>();
        boton_nuevo_Usuario = new javax.swing.JButton();
        boton_eliminar_Usuario = new javax.swing.JButton();
        boton_guardar_usuario = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableUsuarios = new javax.swing.JTable();
        jPanel18 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldCodigo_Solicitante = new javax.swing.JTextField();
        jTextFieldNombre_solicitante = new javax.swing.JTextField();
        jTextFieldCorreo = new javax.swing.JTextField();
        jComboBoxTipo_de_usuario = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldID_Administrador = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTextFieldProposito_Prestamo = new javax.swing.JTextField();
        jSpinnerFecha_Prestamo = new javax.swing.JSpinner();
        jSpinnerFecha_devolucion = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        ID_Prestamo = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jButtonAgregar_Equipo = new javax.swing.JButton();
        jButtonQuitar_Equipo = new javax.swing.JButton();
        jButtonRegistrar_Equipo = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTablePrestamo = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableDetalle_Prestamo = new javax.swing.JTable();
        jButtonGuardar_Prestamos = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jTextField3 = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jListPanel_de_administracion = new javax.swing.JList<>();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jPanel19 = new javax.swing.JPanel();
        jButtonReporte_PDF = new javax.swing.JButton();
        jButtonreporte_excel = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        Etiqueta_de_Bienvenida = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Menu.setBackground(new java.awt.Color(153, 0, 0));
        Menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/process_6623013.png"))); // NOI18N

        jButtonGestion_Usuarios.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonGestion_Usuarios.setText("Gestion de usuarios");
        jButtonGestion_Usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGestion_UsuariosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gestion_usuariosLayout = new javax.swing.GroupLayout(gestion_usuarios);
        gestion_usuarios.setLayout(gestion_usuariosLayout);
        gestion_usuariosLayout.setHorizontalGroup(
            gestion_usuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gestion_usuariosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonGestion_Usuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        gestion_usuariosLayout.setVerticalGroup(
            gestion_usuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButtonGestion_Usuarios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(gestion_usuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 230, 40));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/video_camera_6127171_1.png"))); // NOI18N

        jButtonGestion_Equipos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonGestion_Equipos.setText("Gestión de Equipos");
        jButtonGestion_Equipos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGestion_EquiposActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Gestion_equiposLayout = new javax.swing.GroupLayout(Gestion_equipos);
        Gestion_equipos.setLayout(Gestion_equiposLayout);
        Gestion_equiposLayout.setHorizontalGroup(
            Gestion_equiposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Gestion_equiposLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonGestion_Equipos, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(259, 259, 259))
        );
        Gestion_equiposLayout.setVerticalGroup(
            Gestion_equiposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButtonGestion_Equipos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(Gestion_equipos, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 230, 40));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/Gemini_Generated_Image_9y10fv9y10fv9y10.png"))); // NOI18N

        jButtonRegistro_prestamos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonRegistro_prestamos.setText("  Registro Préstamos");
        jButtonRegistro_prestamos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistro_prestamosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PrestamoLayout = new javax.swing.GroupLayout(Prestamo);
        Prestamo.setLayout(PrestamoLayout);
        PrestamoLayout.setHorizontalGroup(
            PrestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PrestamoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRegistro_prestamos, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PrestamoLayout.setVerticalGroup(
            PrestamoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButtonRegistro_prestamos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(Prestamo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 230, 50));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/delivery_service_9198471.png"))); // NOI18N

        jButtonRegistro_devolucion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonRegistro_devolucion.setText("Registro Devoluciones");
        jButtonRegistro_devolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistro_devolucionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DevolucionesLayout = new javax.swing.GroupLayout(Devoluciones);
        Devoluciones.setLayout(DevolucionesLayout);
        DevolucionesLayout.setHorizontalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DevolucionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRegistro_devolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        DevolucionesLayout.setVerticalGroup(
            DevolucionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButtonRegistro_devolucion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(Devoluciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 230, 50));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/analytic_7442944.png"))); // NOI18N

        jButtonReporte_analisis.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonReporte_analisis.setText("Reporte y analisis");
        jButtonReporte_analisis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReporte_analisisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Reportes_analisisLayout = new javax.swing.GroupLayout(Reportes_analisis);
        Reportes_analisis.setLayout(Reportes_analisisLayout);
        Reportes_analisisLayout.setHorizontalGroup(
            Reportes_analisisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Reportes_analisisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReporte_analisis, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        Reportes_analisisLayout.setVerticalGroup(
            Reportes_analisisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButtonReporte_analisis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(Reportes_analisis, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 270, 230, 50));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jTextFieldBuscar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabelBuscar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelBuscar.setText("Buscar Equipos");

        jButtonBuscar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonBuscar.setText("Busqueda");
        jButtonBuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        Tabla_activos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Equipo", "Nombre", "Marca", "Modelo/serie", "Ubicacion_actual", "Estado", "Fecha_adquisicion", "Observaciones "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        Tabla_activos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        Tabla_activos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Tabla_activos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabla_activosMouseClicked(evt);
            }
        });
        Tabla_activos.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                Tabla_activosVetoableChange(evt);
            }
        });
        jScrollPane1.setViewportView(Tabla_activos);
        if (Tabla_activos.getColumnModel().getColumnCount() > 0) {
            Tabla_activos.getColumnModel().getColumn(3).setMinWidth(50);
            Tabla_activos.getColumnModel().getColumn(3).setPreferredWidth(120);
            Tabla_activos.getColumnModel().getColumn(3).setMaxWidth(120);
            Tabla_activos.getColumnModel().getColumn(4).setMinWidth(50);
            Tabla_activos.getColumnModel().getColumn(4).setPreferredWidth(120);
            Tabla_activos.getColumnModel().getColumn(4).setMaxWidth(120);
            Tabla_activos.getColumnModel().getColumn(6).setMinWidth(50);
            Tabla_activos.getColumnModel().getColumn(6).setPreferredWidth(120);
            Tabla_activos.getColumnModel().getColumn(6).setMaxWidth(120);
            Tabla_activos.getColumnModel().getColumn(7).setMinWidth(200);
            Tabla_activos.getColumnModel().getColumn(7).setPreferredWidth(250);
            Tabla_activos.getColumnModel().getColumn(7).setMaxWidth(250);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabelBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBuscar)
                        .addGap(0, 30, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBuscar)
                    .addComponent(jTextFieldBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("ID");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Nombre");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Marca");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Modelo");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Estado");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Fecha de adquisicion");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Observaciones");

        jTextFieldID.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTextFieldNombre.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTextFieldMarca.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTextFieldModelo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jComboBoxEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponible", "Dañado", "Con fallas" }));
        jComboBoxEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEstadoActionPerformed(evt);
            }
        });

        jTextAreaObservaciones.setColumns(20);
        jTextAreaObservaciones.setRows(5);
        jScrollPane2.setViewportView(jTextAreaObservaciones);

        Boton_nuevo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Boton_nuevo.setText("Nuevo");
        Boton_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_nuevoActionPerformed(evt);
            }
        });

        Boton_guardar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Boton_guardar.setText("Guardar");
        Boton_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_guardarActionPerformed(evt);
            }
        });

        Boton_eliminar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Boton_eliminar.setText("Eliminar");
        Boton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_eliminarActionPerformed(evt);
            }
        });

        Boton_cancelar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Boton_cancelar.setText("Cancelar");
        Boton_cancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancelarActionPerformed(evt);
            }
        });

        jSpinnerFecha_de_adquisicion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jSpinnerFecha_de_adquisicion.setModel(new javax.swing.SpinnerDateModel());
        jSpinnerFecha_de_adquisicion.setEditor(new javax.swing.JSpinner.DateEditor(jSpinnerFecha_de_adquisicion, "yyyy-MM-dd"));

        Ubicacion_actual.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setText("Ubicacion Actual");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(Boton_nuevo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(Boton_guardar)
                                .addGap(46, 46, 46)
                                .addComponent(Boton_eliminar)
                                .addGap(42, 42, 42)
                                .addComponent(Boton_cancelar))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Ubicacion_actual)
                                    .addComponent(jTextFieldID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(jTextFieldNombre)
                                    .addComponent(jTextFieldMarca)
                                    .addComponent(jTextFieldModelo, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSpinnerFecha_de_adquisicion, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(18, 18, 18))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3))
                                    .addComponent(jTextFieldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10))
                            .addComponent(jTextFieldMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11))
                    .addComponent(jTextFieldModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(Ubicacion_actual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jSpinnerFecha_de_adquisicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel14))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Boton_eliminar)
                    .addComponent(Boton_cancelar)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Boton_guardar)
                        .addComponent(Boton_nuevo)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(194, 194, 194))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
        );

        jTabbedPane1.addTab("Gestios de equipos", jPanel5);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("ID");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("Nombre");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Contraseña");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Rol");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Nombre Completo");

        Id_cajadetexto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        caja_texto_nombre.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        caja_texto_nombre_completo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        caja_texto_contraseña.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        combo_box_Rol.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        combo_box_Rol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Monitor" }));
        combo_box_Rol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_box_RolActionPerformed(evt);
            }
        });

        boton_nuevo_Usuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        boton_nuevo_Usuario.setText("Nuevo");
        boton_nuevo_Usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_nuevo_UsuarioActionPerformed(evt);
            }
        });

        boton_eliminar_Usuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        boton_eliminar_Usuario.setText("Eliminar");
        boton_eliminar_Usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_eliminar_UsuarioActionPerformed(evt);
            }
        });

        boton_guardar_usuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        boton_guardar_usuario.setText("Guardar");
        boton_guardar_usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_guardar_usuarioActionPerformed(evt);
            }
        });

        jTableUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Contraseña", "Nombre Completo", "Rol"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableUsuarios);
        if (jTableUsuarios.getColumnModel().getColumnCount() > 0) {
            jTableUsuarios.getColumnModel().getColumn(0).setHeaderValue("ID");
            jTableUsuarios.getColumnModel().getColumn(4).setMinWidth(50);
            jTableUsuarios.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTableUsuarios.getColumnModel().getColumn(4).setMaxWidth(120);
        }

        jPanel18.setBackground(new java.awt.Color(153, 0, 0));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 226, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(56, 56, 56)
                                .addComponent(Id_cajadetexto, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(caja_texto_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(91, 91, 91)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(combo_box_Rol, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(caja_texto_contraseña))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(caja_texto_nombre_completo, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(boton_nuevo_Usuario)
                                .addGap(27, 27, 27)
                                .addComponent(boton_eliminar_Usuario)
                                .addGap(30, 30, 30)
                                .addComponent(boton_guardar_usuario)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(jLabel17)
                                .addComponent(jLabel19)
                                .addComponent(caja_texto_nombre_completo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(caja_texto_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Id_cajadetexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel16)
                                .addComponent(jLabel18)
                                .addComponent(caja_texto_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(combo_box_Rol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(boton_nuevo_Usuario)
                                .addComponent(boton_eliminar_Usuario)
                                .addComponent(boton_guardar_usuario)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 97, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Gestion de usuarios", jPanel1);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Codigo o cedula");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("Nombre");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("Tipo de usuario");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setText("Correo");

        jTextFieldCodigo_Solicitante.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTextFieldNombre_solicitante.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNombre_solicitante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNombre_solicitanteActionPerformed(evt);
            }
        });

        jTextFieldCorreo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jComboBoxTipo_de_usuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxTipo_de_usuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Docente", "Estudiante", "" }));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("ID Administrador");

        jTextFieldID_Administrador.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldID_Administrador, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldNombre_solicitante, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxTipo_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldCodigo_Solicitante, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCodigo_Solicitante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(52, 52, 52)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldNombre_solicitante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jComboBoxTipo_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextFieldCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldID_Administrador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("Proposito");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("Fecha de devolucion");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setText("Fecha de prestamo");

        jTextFieldProposito_Prestamo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jSpinnerFecha_Prestamo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jSpinnerFecha_Prestamo.setModel(new javax.swing.SpinnerDateModel());

        jSpinnerFecha_devolucion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jSpinnerFecha_devolucion.setModel(new javax.swing.SpinnerDateModel());

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel30.setText("ID_Prestamo");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel30)
                    .addComponent(jLabel24))
                .addGap(45, 45, 45)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldProposito_Prestamo)
                    .addComponent(jSpinnerFecha_devolucion, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinnerFecha_Prestamo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ID_Prestamo)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldProposito_Prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addGap(46, 46, 46)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(ID_Prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jSpinnerFecha_Prestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(125, 125, 125)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jSpinnerFecha_devolucion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(136, 136, 136))
        );

        jButtonAgregar_Equipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonAgregar_Equipo.setText("Agregar Equipo");
        jButtonAgregar_Equipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregar_EquipoActionPerformed(evt);
            }
        });

        jButtonQuitar_Equipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonQuitar_Equipo.setText("Quitar Equipo");
        jButtonQuitar_Equipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQuitar_EquipoActionPerformed(evt);
            }
        });

        jButtonRegistrar_Equipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonRegistrar_Equipo.setText("Registrar Prestamo");
        jButtonRegistrar_Equipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrar_EquipoActionPerformed(evt);
            }
        });

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTablePrestamo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Activo", "Nombre Equipo", "Marca", "N° de Serie"
            }
        ));
        jTablePrestamo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(jTablePrestamo);
        if (jTablePrestamo.getColumnModel().getColumnCount() > 0) {
            jTablePrestamo.getColumnModel().getColumn(0).setMinWidth(100);
            jTablePrestamo.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTablePrestamo.getColumnModel().getColumn(0).setMaxWidth(150);
            jTablePrestamo.getColumnModel().getColumn(1).setMinWidth(100);
            jTablePrestamo.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTablePrestamo.getColumnModel().getColumn(1).setMaxWidth(150);
            jTablePrestamo.getColumnModel().getColumn(2).setMinWidth(100);
            jTablePrestamo.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTablePrestamo.getColumnModel().getColumn(2).setMaxWidth(150);
            jTablePrestamo.getColumnModel().getColumn(3).setMinWidth(100);
            jTablePrestamo.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTablePrestamo.getColumnModel().getColumn(3).setMaxWidth(150);
        }

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTableDetalle_Prestamo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                " ID Prestamo", "ID activo", "Nombre Equipo", "Marca", "Modelo/serie", "ID Aministrador/Monitor", "Fecha de Préstamo", "Fecha de Devolución Estimada", "Fecha de Devolución Real", "Propósito", "Codigo", "Nombre del Solicitante", "Tipo de Usuario", "Correo Solicitante"
            }
        ));
        jTableDetalle_Prestamo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane5.setViewportView(jTableDetalle_Prestamo);
        if (jTableDetalle_Prestamo.getColumnModel().getColumnCount() > 0) {
            jTableDetalle_Prestamo.getColumnModel().getColumn(5).setMinWidth(100);
            jTableDetalle_Prestamo.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(5).setMaxWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(6).setMinWidth(100);
            jTableDetalle_Prestamo.getColumnModel().getColumn(6).setPreferredWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(6).setMaxWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(7).setMinWidth(100);
            jTableDetalle_Prestamo.getColumnModel().getColumn(7).setPreferredWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(7).setMaxWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(10).setMinWidth(100);
            jTableDetalle_Prestamo.getColumnModel().getColumn(10).setPreferredWidth(150);
            jTableDetalle_Prestamo.getColumnModel().getColumn(10).setMaxWidth(150);
        }

        jButtonGuardar_Prestamos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonGuardar_Prestamos.setText("Guardar datos ");
        jButtonGuardar_Prestamos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardar_PrestamosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jButtonAgregar_Equipo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonQuitar_Equipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonGuardar_Prestamos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButtonRegistrar_Equipo))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonAgregar_Equipo)
                            .addComponent(jButtonRegistrar_Equipo)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addComponent(jButtonGuardar_Prestamos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonQuitar_Equipo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 74, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Registro de Prestamos", jPanel3);

        jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", " Solicitante/Usuario", "Fecha Préstamo", "Fecha Devolución Estimada"
            }
        ));
        jTable6.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane9.setViewportView(jTable6);
        if (jTable6.getColumnModel().getColumnCount() > 0) {
            jTable6.getColumnModel().getColumn(0).setMinWidth(100);
            jTable6.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable6.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable6.getColumnModel().getColumn(1).setMinWidth(100);
            jTable6.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable6.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable6.getColumnModel().getColumn(2).setMinWidth(100);
            jTable6.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable6.getColumnModel().getColumn(2).setMaxWidth(150);
            jTable6.getColumnModel().getColumn(3).setMinWidth(100);
            jTable6.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable6.getColumnModel().getColumn(3).setMaxWidth(150);
        }

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Activo", "Nombre", "Marca"
            }
        ));
        jTable5.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane6.setViewportView(jTable5);
        if (jTable5.getColumnModel().getColumnCount() > 0) {
            jTable5.getColumnModel().getColumn(0).setMinWidth(100);
            jTable5.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable5.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable5.getColumnModel().getColumn(1).setMinWidth(100);
            jTable5.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable5.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable5.getColumnModel().getColumn(2).setMinWidth(100);
            jTable5.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable5.getColumnModel().getColumn(2).setMaxWidth(150);
        }

        jComboBox1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bien ", "Anomalias/Fallas", " " }));

        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jButton10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton10.setText("Corfirmar Devolucion");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setText("                   Fecha de devolucion");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, 206, Short.MAX_VALUE)))
                .addContainerGap(77, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Registro de devoluciones", jPanel2);

        jListPanel_de_administracion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jListPanel_de_administracion.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Análisis de Fallas y Mantenimiento", "Análisis de Uso y Demanda", "Análisis de Comportamiento del Usuario", "Historial de Activos", " " };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane7.setViewportView(jListPanel_de_administracion);

        jScrollPane10.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane10.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Activo", "Nombre de Equipo", "Modelo", "Cantidad de Fallas"
            }
        ));
        jTable4.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane10.setViewportView(jTable4);
        if (jTable4.getColumnModel().getColumnCount() > 0) {
            jTable4.getColumnModel().getColumn(0).setMinWidth(100);
            jTable4.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable4.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable4.getColumnModel().getColumn(1).setMinWidth(100);
            jTable4.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable4.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable4.getColumnModel().getColumn(2).setMinWidth(100);
            jTable4.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable4.getColumnModel().getColumn(2).setMaxWidth(150);
            jTable4.getColumnModel().getColumn(3).setMinWidth(100);
            jTable4.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable4.getColumnModel().getColumn(3).setMaxWidth(150);
        }

        jScrollPane11.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane11.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Activo", "Nombre de Equipo", "Veces Prestado", "Última vez Préstado", "Estado actual"
            }
        ));
        jTable7.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane11.setViewportView(jTable7);
        if (jTable7.getColumnModel().getColumnCount() > 0) {
            jTable7.getColumnModel().getColumn(0).setMinWidth(100);
            jTable7.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable7.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable7.getColumnModel().getColumn(1).setMinWidth(100);
            jTable7.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable7.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable7.getColumnModel().getColumn(2).setMinWidth(100);
            jTable7.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable7.getColumnModel().getColumn(2).setMaxWidth(150);
            jTable7.getColumnModel().getColumn(3).setMinWidth(100);
            jTable7.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable7.getColumnModel().getColumn(3).setMaxWidth(150);
            jTable7.getColumnModel().getColumn(4).setMinWidth(100);
            jTable7.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable7.getColumnModel().getColumn(4).setMaxWidth(150);
        }

        jScrollPane12.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane12.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Préstamo", "Solicitante", "Fecha Devolución Estimada", "Fecha Devolución Real", "Días/Horas de Retraso"
            }
        ));
        jTable8.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane12.setViewportView(jTable8);
        if (jTable8.getColumnModel().getColumnCount() > 0) {
            jTable8.getColumnModel().getColumn(0).setMinWidth(100);
            jTable8.getColumnModel().getColumn(0).setPreferredWidth(150);
            jTable8.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable8.getColumnModel().getColumn(1).setMinWidth(100);
            jTable8.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable8.getColumnModel().getColumn(1).setMaxWidth(150);
            jTable8.getColumnModel().getColumn(2).setMinWidth(100);
            jTable8.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable8.getColumnModel().getColumn(2).setMaxWidth(150);
            jTable8.getColumnModel().getColumn(3).setMinWidth(100);
            jTable8.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable8.getColumnModel().getColumn(3).setMaxWidth(150);
            jTable8.getColumnModel().getColumn(4).setMinWidth(100);
            jTable8.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable8.getColumnModel().getColumn(4).setMaxWidth(150);
        }

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );

        jButtonReporte_PDF.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonReporte_PDF.setText("Descargar Reporte en PDF");

        jButtonreporte_excel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonreporte_excel.setText("Descargar Reporte en excel");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jButtonReporte_PDF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonreporte_excel)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReporte_PDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonreporte_excel))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Reporte y analisis", jPanel4);

        jPanel7.setBackground(new java.awt.Color(153, 0, 0));

        Etiqueta_de_Bienvenida.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Etiqueta_de_Bienvenida.setForeground(new java.awt.Color(255, 255, 255));
        Etiqueta_de_Bienvenida.setText("Hola + usuario Bienvenido al sistema de gestion de recursos audiovisuales ");

        jButton1.setText("Salir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(Etiqueta_de_Bienvenida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(79, 79, 79))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1)
                    .addComponent(Etiqueta_de_Bienvenida))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMAGENES/Logo_universidad.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Menu, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Menu, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRegistro_devolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistro_devolucionActionPerformed
        // 1. Actualiza el índice anterior.
    indicePestanaAnterior = 3; // El índice deseado es 3
    
    // 2. Realiza el cambio de pestaña.
    jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_jButtonRegistro_devolucionActionPerformed

    private void jButtonReporte_analisisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReporte_analisisActionPerformed
        // 1. Actualiza el índice anterior.
    indicePestanaAnterior = 4; // El índice deseado es 4
    
    // 2. Realiza el cambio de pestaña.
    jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_jButtonReporte_analisisActionPerformed
    
    private void jButtonGestion_EquiposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGestion_EquiposActionPerformed
    // PASO 1: Establecer el índice deseado en la variable de control.
    // Esto "autoriza" el cambio antes de que el evento StateChanged se dispare.
    indicePestanaAnterior = 0; 
    
    // PASO 2: Realizar el cambio de pestaña.
    jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jButtonGestion_EquiposActionPerformed

    private void jButtonRegistro_prestamosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistro_prestamosActionPerformed

    indicePestanaAnterior = 2; // El índice deseado es 2
    
    // 2. Realiza el cambio de pestaña.
    jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButtonRegistro_prestamosActionPerformed

    private void jButtonGestion_UsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGestion_UsuariosActionPerformed
        // 1. Actualiza el índice anterior.
    indicePestanaAnterior = 1; // El índice deseado es 1
    
    // 2. Realiza el cambio de pestaña.
    jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButtonGestion_UsuariosActionPerformed
    
    private void jButtonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarActionPerformed
        String terminoBusqueda = jTextFieldBuscar.getText().trim();

    if (terminoBusqueda.isEmpty()) {
        // Si el campo de búsqueda está vacío, recargar todos los equipos (estado inicial)
        cargarActivosEnTabla(); 
        javax.swing.JOptionPane.showMessageDialog(this,
            "Mostrando todos los equipos disponibles.",
            "Búsqueda Limpia",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    try {
        // 1. Llama al método de búsqueda REAL en tu controlador
        List<modelo.Equipos> resultados = controladorPrincipal.buscarPorCriterio(terminoBusqueda); 

        // 2. Cargar la tabla con los resultados (usando el método sobrecargado)
        cargarActivosEnTabla(resultados); 

        if (resultados.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No se encontraron activos que coincidan con la búsqueda: \"" + terminoBusqueda + "\".",
                "Búsqueda sin resultados",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
             javax.swing.JOptionPane.showMessageDialog(this,
                 "Se encontraron " + resultados.size() + " activos que coinciden con la búsqueda.",
                 "Búsqueda exitosa",
                 javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception e) {
        // Manejo de errores de base de datos o controlador
        java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, "Error en la búsqueda", e);
        
        javax.swing.JOptionPane.showMessageDialog(this,
            "Error grave al procesar la búsqueda: " + e.getMessage(),
            "Error de Base de Datos",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButtonBuscarActionPerformed

    private void jComboBoxEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxEstadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxEstadoActionPerformed

    private void combo_box_RolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_box_RolActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_box_RolActionPerformed

    private void jTextFieldNombre_solicitanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombre_solicitanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombre_solicitanteActionPerformed

    private void Boton_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_nuevoActionPerformed
        // Llama a la lógica de limpieza centralizada
    limpiarCampos(); 
    
    // B. El botón Guardar ya está habilitado en limpiarCampos.
    // El botón Eliminar ya está deshabilitado en limpiarCampos.

    // C. Opcional: enfocar el cursor (ya está en limpiarCampos, pero puedes dejarlo aquí si quieres)
    jTextFieldNombre.requestFocus();
    
    // D. MOSTRAR EL MENSAJE DE CONFIRMACIÓN
    JOptionPane.showMessageDialog(this, 
        "Modo CREACIÓN activado. Ingrese los datos y presione Guardar.", 
        "Nuevo Activo", 
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_Boton_nuevoActionPerformed
    
    private void Boton_cancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancelarActionPerformed
    // La acción de Cancelar es simplemente restablecer la interfaz.
    // Esto se logra llamando al método centralizado.
    limpiarCampos();
    // 2. Opcional: Mostrar un mensaje si lo deseas
    JOptionPane.showMessageDialog(this, 
        "Operación cancelada. Campos restablecidos.", 
        "Cancelado", 
        JOptionPane.INFORMATION_MESSAGE);
    // 1. Limpiar todos los campos de texto
    jTextFieldID.setText("");
    jTextFieldNombre.setText("");
    jTextFieldMarca.setText("");
    jTextFieldModelo.setText("");
    Ubicacion_actual.setText("");
    // Si tienes el campo de ubicación:
    // jTextFieldUbicacion.setText(""); 
    
    // 2. Limpiar el área de texto de observaciones
    jTextAreaObservaciones.setText(""); 
    
    // 3. Restablecer los componentes a un estado por defecto
    jComboBoxEstado.setSelectedIndex(0); // Restablecer al primer estado ("Disponible")
    
    // El JSpinner puede restablecerse a la fecha actual o a una fecha inicial,
    // asumiendo que el modelo del spinner ya está configurado para Date
    jSpinnerFecha_de_adquisicion.setValue(new java.util.Date()); 
    
    // 4. Desactivar botones de edición/eliminación si es necesario (Opcional, si controlas el estado)
    // Boton_guardar.setEnabled(false); 
    // Boton_eliminar.setEnabled(false);
    // Boton_nuevo.setEnabled(true);
    }//GEN-LAST:event_Boton_cancelarActionPerformed

    private void Boton_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_guardarActionPerformed
        // 1. Crear el objeto 'equipo' con los datos del formulario (como ya haces)
    Equipos equipo = new Equipos();
    
    try {
        boolean esEdicion = !jTextFieldID.getText().isEmpty();

        // Si es edición, toma el ID
        if (esEdicion) {
            equipo.setIDEquipo(Integer.parseInt(jTextFieldID.getText()));
        }
        
        // Asignar el resto de valores
        equipo.setNombre(jTextFieldNombre.getText());
        equipo.setMarca(jTextFieldMarca.getText());
        equipo.setModeloSerie(jTextFieldModelo.getText());
        equipo.setUbicacionactual(Ubicacion_actual.getText()); // Usando tu campo Ubicacion_actual
        equipo.setEstado(jComboBoxEstado.getSelectedItem().toString());
        equipo.setObservaciones(jTextAreaObservaciones.getText());
        String observaciones = jTextAreaObservaciones.getText();
        equipo.setObservaciones(observaciones);
        
        // Convertir el valor del JSpinner a Date
        Date fechaAdquisicion = (Date) jSpinnerFecha_de_adquisicion.getValue();
        equipo.setFechaadquisicion(fechaAdquisicion);


        // 2. LLAMADA AL CONTROLADOR CENTRALIZADO (¡CLAVE!)
        controladorPrincipal.guardarEquipo(equipo); // <--- ESTO DEBE SER LA ÚNICA LLAMADA DE GUARDADO

        // 3. Mostrar éxito y actualizar la interfaz
        String mensaje = esEdicion ? "actualizado" : "guardado";
        JOptionPane.showMessageDialog(this, "Equipo " + mensaje + " con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
        limpiarCampos();
        cargarActivosEnTabla(); // Recarga la tabla para ver el cambio
        

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        
    } catch (Exception e) {
        // Si el error "Not supported yet." viene aquí, es 100% que la llamada 
        // 'controlador.guardarEquipo(equipo)' está ejecutando un método mal implementado.
        logger.log(java.util.logging.Level.SEVERE, "Error al guardar (editar) activo: " + e.getMessage(), e);
        JOptionPane.showMessageDialog(this, "Error al procesar la operación: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_Boton_guardarActionPerformed

    private void Boton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_eliminarActionPerformed
        // 1. VALIDACIÓN BÁSICA
    String idText = jTextFieldID.getText().trim();
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un activo para poder eliminarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // 2. CONFIRMACIÓN (LÍNEA CORREGIDA)
    int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el activo con ID: " + idText + "?",
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, // Argumento 4
            JOptionPane.QUESTION_MESSAGE); // ⬅️ Argumento 5: Agregado el tipo de mensaje
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        // Declaramos idEquipo fuera del try para usarlo en el catch
        Integer idEquipo = null;
        try {
            // 3. OBTENER ID y EJECUTAR ELIMINACIÓN
            idEquipo = Integer.parseInt(idText); 
            
            controladorPrincipal.eliminarEquipo(idEquipo); 
            
            // 4. ACCIONES POST-ELIMINACIÓN
            cargarActivosEnTabla();
            JOptionPane.showMessageDialog(this, "✅ Activo con ID " + idEquipo + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            Boton_cancelarActionPerformed(null); 
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ El ID del activo no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (javax.persistence.EntityNotFoundException e) {
            JOptionPane.showMessageDialog(this, "❌ El activo con ID " + idEquipo + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.severe("Error al eliminar activo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "❌ Error grave al eliminar el activo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_Boton_eliminarActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        int indiceActual = jTabbedPane1.getSelectedIndex();

    // Si el índice actual (el que el JTabbedPane intenta mostrar) es diferente
    // del índice que nuestros botones establecieron por última vez:
    if (indiceActual != indicePestanaAnterior) {
        // Bloquear el cambio: Forzar el índice a volver al índice anterior.
        jTabbedPane1.setSelectedIndex(indicePestanaAnterior);

        // ¡IMPORTANTE! NO actualices indicePestanaAnterior aquí.
        // Si lo haces, el cambio se bloquea y el valor se mantiene.
    } else {
        // Si el índice es el mismo, significa que el cambio fue legítimo (viene de un botón
        // que ya actualizó indicePestanaAnterior) o la primera carga.
        // En realidad, esta línea es opcional si el botón ya actualiza la variable.
        // indicePestanaAnterior = indiceActual; 
    }
    }//GEN-LAST:event_jTabbedPane1StateChanged
    private void cargarActivosEnTabla() {
    // 1. Verificar si la conexión fue exitosa
    if (controladorPrincipal == null || !controladorPrincipal.isConexionExitosa()) {
        logger.warning("No se puede cargar la tabla. La conexión JPA falló al inicio.");
        return; 
    }

    try {
        // 2. Obtener la lista de todos los equipos a través de la capa de control
        List<Equipos> listaEquipos = controladorPrincipal.obtenerTodosLosEquipos(); 
        
        // 3. Llamar al método auxiliar para hacer el trabajo de llenado
        llenarTablaConLista(listaEquipos);
        
    } catch (Exception ex) {
        // Manejo de errores al consultar la base de datos
        JOptionPane.showMessageDialog(this, 
            "Error al cargar los equipos: " + ex.getMessage(), 
            "Error de Datos", JOptionPane.ERROR_MESSAGE);
        logger.severe("Error al cargar equipos: " + ex.getMessage());
    }
    }
    private void cargarTablaUsuarios() {
    if (controladorPrincipal == null || !controladorPrincipal.isConexionExitosa()) {
        logger.warning("No se puede cargar la tabla de usuarios. La conexión falló.");
        return;
    }
    try {
        List<Usuario> listaUsuarios = controladorPrincipal.obtenerTodosLosUsuarios();
        llenarTablaUsuariosConLista(listaUsuarios);
    } catch (Exception ex) {
        logger.severe("Error al cargar la tabla de usuarios: " + ex.getMessage());
        // Opcional: Mostrar un JOptionPane aquí.
    }
    }
    private void Tabla_activosVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_Tabla_activosVetoableChange
        // TODO add your handling code here:
    }//GEN-LAST:event_Tabla_activosVetoableChange

    private void Tabla_activosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabla_activosMouseClicked
        // 1. Obtener la fila seleccionada
    int filaSeleccionada = Tabla_activos.getSelectedRow();
    
    // Validar que se haya seleccionado una fila
    if (filaSeleccionada >= 0) {
        try {
            // 2. Obtener el modelo de la tabla
            DefaultTableModel modelo = (DefaultTableModel) Tabla_activos.getModel();

            // 3. Extraer los datos de la fila seleccionada
            
            // ID Equipo (Integer/Long) - Columna 0
            String idStr = (modelo.getValueAt(filaSeleccionada, 0) != null) ? modelo.getValueAt(filaSeleccionada, 0).toString() : "";
            
            // Nombre, Marca, Modelo/Serie (Columnas 1, 2, 3)
            String nombre = modelo.getValueAt(filaSeleccionada, 1).toString();
            String marca = modelo.getValueAt(filaSeleccionada, 2).toString();
            String modeloSerie = modelo.getValueAt(filaSeleccionada, 3).toString();
            
            // Ubicación Actual (String) - Columna 4
            String ubicacionActual = modelo.getValueAt(filaSeleccionada, 4).toString();
            
            // Estado (String) - Columna 5
            String estado = modelo.getValueAt(filaSeleccionada, 5).toString();
            
            // Fecha Adquisición (String o Date) - Columna 6
            Object fechaObj = modelo.getValueAt(filaSeleccionada, 6);
            String fechaStr = (fechaObj != null) ? fechaObj.toString() : "N/A";


            // 4. Cargar los datos en los campos de texto
            jTextFieldID.setText(idStr);
            jTextFieldNombre.setText(nombre);
            jTextFieldMarca.setText(marca);
            jTextFieldModelo.setText(modeloSerie);
            
            // **IMPORTANTE:** Asignar la Ubicación Actual a tu nuevo campo de texto
            // (Reemplaza 'jTextFieldUbicacion_actual' con el nombre de tu componente real)
            // Si no tiene el campo aún, DEBE comentar esta línea para evitar errores.
            // jTextFieldUbicacion_actual.setText(ubicacionActual); 
            
            // 5. Establecer el valor del ComboBox Estado
            jComboBoxEstado.setSelectedItem(estado);
            
            // 6. Establecer el valor del Spinner de Fecha
            if (!fechaStr.equals("N/A")) {
                // CORRECCIÓN CLAVE: Usamos un formato más robusto para manejar el timestamp de la DB
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
                Date fecha = sdf.parse(fechaStr);
                
                // NOTA: Es posible que necesites asegurar que tu JSpinner usa un SpinnerDateModel.
                jSpinnerFecha_de_adquisicion.setValue(fecha);
            } else {
                // Si la fecha es N/A, restablece a la fecha actual o valor por defecto
                jSpinnerFecha_de_adquisicion.setValue(new Date()); 
            }
            
            // 7. Cargar las observaciones
            jTextAreaObservaciones.setText("");
            
        } catch (java.text.ParseException pe) {
             // Este catch maneja específicamente los errores de formato de fecha
             JOptionPane.showMessageDialog(this, "Error de formato de fecha al cargar: " + pe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             logger.severe("Error de Parseo de Fecha: " + pe.getMessage());
        } catch (Exception ex) {
            // Este catch maneja el "illegal value" u otros errores
            JOptionPane.showMessageDialog(this, "Error al cargar datos del Activo (Valor no permitido): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.severe("Error al hacer clic en la tabla: " + ex.getMessage());
        }
    }
    }//GEN-LAST:event_Tabla_activosMouseClicked

    private void boton_nuevo_UsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_nuevo_UsuarioActionPerformed
        limpiarCamposUsuario(); 
        caja_texto_nombre.requestFocus();
    
    JOptionPane.showMessageDialog(this, 
        "Modo CREACIÓN activado. Ingrese los datos del nuevo usuario y presione Guardar.", 
        "Nuevo Usuario", 
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_boton_nuevo_UsuarioActionPerformed

    private void boton_eliminar_UsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_eliminar_UsuarioActionPerformed
        String idText = Id_cajadetexto.getText().trim();
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para poder eliminarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el usuario con ID: " + idText + "?",
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        Integer idUsuario = null;
        try {
            idUsuario = Integer.parseInt(idText); 
            
            controladorPrincipal.eliminarUsuario(idUsuario); 
            
            cargarTablaUsuarios();
            JOptionPane.showMessageDialog(this, "✅ Usuario con ID " + idUsuario + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCamposUsuario(); // Limpia los campos
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ El ID del usuario no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (javax.persistence.EntityNotFoundException e) { 
            // Si el controlador JPA lanza un EntityNotFoundException, lo manejamos aquí.
            JOptionPane.showMessageDialog(this, "❌ El usuario con ID " + idUsuario + " no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.severe("Error al eliminar usuario: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "❌ Error grave al eliminar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_boton_eliminar_UsuarioActionPerformed

    private void boton_guardar_usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_guardar_usuarioActionPerformed
        // Obtener el valor del campo de login que usaremos para buscar
    String nombreUsuarioLogin = caja_texto_nombre.getText().trim();
    
    try {
        if (nombreUsuarioLogin.isEmpty()) {
            // No podemos guardar ni buscar si el campo clave está vacío
            throw new IllegalArgumentException("El campo Nombre de Usuario no puede estar vacío.");
        }
        
        // 1. INTENTAR ENCONTRAR UN USUARIO EXISTENTE POR EL LOGIN
        // Si el controlador encuentra un usuario con ese nombre, significa que es una EDICIÓN.
        modelo.Usuario usuario = controladorPrincipal.obtenerUsuarioPorNombre(nombreUsuarioLogin);
        boolean esEdicion = (usuario != null);
        
        // Si no se encontró, creamos una nueva instancia para CREACIÓN
        if (!esEdicion) {
            usuario = new modelo.Usuario();
        }
        
        // 2. ASIGNAR CAMPOS AL OBJETO USUARIO
        
        // Asignamos el Nombre de Usuario (login), que fue la clave de búsqueda
        usuario.setNombreUsuario(nombreUsuarioLogin); 
        
        usuario.setContrasena(String.valueOf(caja_texto_contraseña.getPassword()));
        usuario.setNombreCompleto(caja_texto_nombre_completo.getText().trim());
        
        
        // 3. ASIGNACIÓN DEL ROL (Usando la búsqueda por nombre)
        Object rolSeleccionado = combo_box_Rol.getSelectedItem();
        
        if (rolSeleccionado == null || rolSeleccionado.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un Rol.");
        }
        String nombreRol = rolSeleccionado.toString().trim();
        
        modelo.Rol rolEncontrado = controladorPrincipal.obtenerRolPorNombre(nombreRol); 

        if (rolEncontrado == null) {
             throw new Exception("El Rol '" + nombreRol + "' no fue encontrado. Verifique la tabla de Roles.");
        }
        usuario.setRolId(rolEncontrado);
        

        // 4. LLAMADA AL CONTROLADOR CENTRAL
        // El método guardarUsuario debe chequear si el objeto tiene ID o no para usar create/edit.
        controladorPrincipal.guardarUsuario(usuario); 

        // 5. Mostrar éxito y actualizar la interfaz
        String mensaje = esEdicion ? "actualizado" : "guardado";
        JOptionPane.showMessageDialog(this, "✅ Usuario " + mensaje + " con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
        limpiarCamposUsuario();
        cargarTablaUsuarios();
        

    } catch (NumberFormatException e) {
        // Este catch atrapa errores si, por ejemplo, NombreUsuario debe ser un Long (número).
        JOptionPane.showMessageDialog(this, "❌ Error de formato: Algún campo numérico tiene un valor inválido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        
    } catch (IllegalArgumentException e) {
        // Captura errores de validación
        JOptionPane.showMessageDialog(this, "❌ Validación: " + e.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        
    } catch (Exception e) {
        // Captura errores de persistencia (ej: si se intenta crear un usuario cuyo NombreUsuario ya existe y está configurado como UNIQUE en la BD)
        logger.severe("Error al guardar (editar) usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "❌ Error al procesar la operación: " + e.getMessage(), "Error de BD/JPA", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_boton_guardar_usuarioActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonAgregar_EquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregar_EquipoActionPerformed
        int filaSeleccionada = jTablePrestamo.getSelectedRow();
    
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un equipo de la lista de disponibles.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        return;
    }

    DefaultTableModel modeloTablaPrestamo = (DefaultTableModel) jTablePrestamo.getModel();
    DefaultTableModel modeloDetalle = (DefaultTableModel) jTableDetalle_Prestamo.getModel(); 

    // Extraer los 4 datos del equipo de jTablePrestamo
    // [0]IDEquipo, [1]Nombre, [2]Marca, [3]ModeloSerie
    Object idActivo = modeloTablaPrestamo.getValueAt(filaSeleccionada, 0);
    Object nombreEquipo = modeloTablaPrestamo.getValueAt(filaSeleccionada, 1);
    Object marca = modeloTablaPrestamo.getValueAt(filaSeleccionada, 2);
    Object modeloSerie = modeloTablaPrestamo.getValueAt(filaSeleccionada, 3);
    
    // Verificar duplicados (comparamos con el ID activo, que está en el índice 1 del detalle)
    for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
        if (modeloDetalle.getValueAt(i, 1) != null && modeloDetalle.getValueAt(i, 1).equals(idActivo)) { 
            JOptionPane.showMessageDialog(this, "El equipo con ID " + idActivo + " ya ha sido agregado.", "Duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    // Preparar la nueva fila para jTableDetalle_Prestamo (14 columnas)
    int numeroColumnasDetalle = modeloDetalle.getColumnCount(); // Debería ser 14
    Object[] nuevaFilaDetalle = new Object[numeroColumnasDetalle];

    // Columna 0: ID Préstamo (se deja NULL)
    nuevaFilaDetalle[0] = null; 

    // Columnas 1 al 4: Datos del equipo (insertados en el orden correcto)
    nuevaFilaDetalle[1] = idActivo;
    nuevaFilaDetalle[2] = nombreEquipo;
    nuevaFilaDetalle[3] = marca;
    nuevaFilaDetalle[4] = modeloSerie;
    
    // Columnas 5 al 13: El resto se quedan como NULL/default hasta que se presionen 'Registrar'
    // Se recomienda inicializar las columnas de texto con "" y otras con null si son opcionales
    // Java ya inicializa Object[] con nulls, así que no es necesario escribir las 9 líneas restantes.

    modeloDetalle.addRow(nuevaFilaDetalle);
    jTablePrestamo.clearSelection();
    }//GEN-LAST:event_jButtonAgregar_EquipoActionPerformed

    private void jButtonQuitar_EquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQuitar_EquipoActionPerformed
        int filaSeleccionada = jTableDetalle_Prestamo.getSelectedRow();
    
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un equipo de la lista inferior para quitarlo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    modeloDetallePrestamo.removeRow(filaSeleccionada);
    }//GEN-LAST:event_jButtonQuitar_EquipoActionPerformed

    private void jButtonRegistrar_EquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrar_EquipoActionPerformed
        // Obtener el modelo de la tabla de detalle
    DefaultTableModel modeloDetalle = (DefaultTableModel) jTableDetalle_Prestamo.getModel(); 

    // 1. Validaciones iniciales
    if (modeloDetalle.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Debe agregar al menos un equipo al detalle del préstamo.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Objeto para formatear las fechas a un String legible (soluciona el problema de la fecha larga)
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // 2. Recolección y validación de datos del formulario (la cabecera del préstamo)
    
    // Columna 0: ID Préstamo
    String idPrestamoStr = ID_Prestamo.getText().trim();
    if (idPrestamoStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El campo ID Préstamo no puede estar vacío.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
        return;
    }
    Object idPrestamo = idPrestamoStr;
    
    // Columna 5: ID Administrador/Monitor
    String monitorIdStr = jTextFieldID_Administrador.getText().trim(); 
    if (monitorIdStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El campo ID Administrador no puede estar vacío.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
        return;
    }
    Object monitorId = monitorIdStr;
    
    // Columna 6: Fecha de Préstamo
    Date dateFechaPrestamo = (Date) jSpinnerFecha_Prestamo.getValue();
    String fechaPrestamo = sdf.format(dateFechaPrestamo);

    // Columna 7: Fecha de Devolución Estimada
    Date dateFechaDevolucionEstimada = (Date) jSpinnerFecha_devolucion.getValue();
    String fechaDevolucionEstimada = sdf.format(dateFechaDevolucionEstimada);

    // Extracción de otros datos
    String proposito = jTextFieldProposito_Prestamo.getText();
    String codigoSolicitante = jTextFieldCodigo_Solicitante.getText();
    String nombreSolicitante = jTextFieldNombre_solicitante.getText();
    String tipoUsuario = (String) jComboBoxTipo_de_usuario.getSelectedItem();
    String correoSolicitante = jTextFieldCorreo.getText();

    // 3. Iterar sobre todas las filas del detalle y actualizar las columnas de cabecera
    
    // Nota: Los índices fueron ajustados debido a la eliminación de la columna 8 (Fecha Devolución Real)
    for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
        
        // Columna 0: ID Préstamo
        modeloDetalle.setValueAt(idPrestamo, i, 0); 
        
        // Columna 5: ID Administrador/Monitor
        modeloDetalle.setValueAt(monitorId, i, 5);
        
        // Columna 6: Fecha de Préstamo (String Formateado)
        modeloDetalle.setValueAt(fechaPrestamo, i, 6);
        
        // Columna 7: Fecha de Devolución Estimada (String Formateado)
        modeloDetalle.setValueAt(fechaDevolucionEstimada, i, 7);
        
        // Columna 8: Propósito (Anteriormente índice 9)
        modeloDetalle.setValueAt(proposito, i, 8);
        
        // Columna 9: Código del solicitante (Anteriormente índice 10)
        modeloDetalle.setValueAt(codigoSolicitante, i, 9);
        
        // Columna 10: Nombre del Solicitante (Anteriormente índice 11)
        modeloDetalle.setValueAt(nombreSolicitante, i, 10);
        
        // Columna 11: Tipo de Usuario (Anteriormente índice 12)
        modeloDetalle.setValueAt(tipoUsuario, i, 11);
        
        // Columna 12: Correo Solicitante (Anteriormente índice 13)
        modeloDetalle.setValueAt(correoSolicitante, i, 12);
    }
    
    // 4. Notificar al usuario
    JOptionPane.showMessageDialog(this, "Datos del préstamo cargados correctamente en todas las filas del detalle.", "Datos Preparados", JOptionPane.INFORMATION_MESSAGE);
    
    // 5. Paso Final: Llamada al controlador para guardar en la base de datos
    // Aquí es donde usualmente llamarías a tu capa de persistencia para guardar 
    // todas las filas de modeloDetalle en la tabla DETALLE_PRESTAMO de tu base de datos.
}

// Método de limpieza para el formulario de préstamo
private void limpiarFormularioPrestamo() {
    // 1. Limpiar campos de texto
    // jTextFieldCodigo_Solicitante.setText(""); // Si no implementa búsqueda
    jTextFieldNombre_solicitante.setText("");
    jTextFieldCorreo.setText("");
    jTextFieldProposito_Prestamo.setText("");
    
    // 2. Restablecer ComboBox
    jComboBoxTipo_de_usuario.setSelectedIndex(0);
    
    // 3. Limpiar la lista de equipos seleccionados
    modeloDetallePrestamo.setRowCount(0);
    
    // 4. Resetear Spinners a la hora actual
    configurarSpinnersFechaHora();
    }//GEN-LAST:event_jButtonRegistrar_EquipoActionPerformed

    private void jButtonGuardar_PrestamosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardar_PrestamosActionPerformed
      try {
        // 1. Recopilar datos (Prestamo - Cabecera)
        // 🚨 CRÍTICO: Asegúrate que jTextFieldID_Administrador contenga un LONG válido (ej: 1, 2, 5)
        Long idMonitor = Long.parseLong(jTextFieldID_Administrador.getText().trim());

        // Datos del solicitante y préstamo
        String nombreSolicitante = jTextFieldNombre_solicitante.getText().trim();
        String tipoUsuario = jComboBoxTipo_de_usuario.getSelectedItem().toString();
        String correo = jTextFieldCorreo.getText().trim();
        String proposito = jTextFieldProposito_Prestamo.getText().trim();
        
        // 🟢 VARIABLE FALTANTE QUE CORRIGE EL ERROR DE FIRMA
        String codigoSolicitante = jTextFieldCodigo_Solicitante.getText().trim(); 

        // Fechas (asumiendo que son Date con hora)
        Date fechaPrestamo = (Date) jSpinnerFecha_Prestamo.getValue();
        Date fechaDevolucionEsperada = (Date) jSpinnerFecha_devolucion.getValue();

        // 2. Obtener la lista de IDs de equipos de la jTableDetalle_Prestamo
        // Esta función toma los IDs de la columna 1
        List<Integer> idsEquipos = obtenerIDsEquiposDeDetalle();

        // Validaciones básicas
        if (idsEquipos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un equipo al detalle del préstamo.", "Error de Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nombreSolicitante.isEmpty() || correo.isEmpty() || proposito.isEmpty() || codigoSolicitante.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de Solicitante y Propósito son obligatorios.", "Error de Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 3. Llamar al controlador (¡con los 9 argumentos!)
        boolean exito = controladorPrincipal.registrarNuevoPrestamo(
            idMonitor,
            nombreSolicitante,
            tipoUsuario,
            correo,
            proposito,
            fechaPrestamo,
            fechaDevolucionEsperada,
            idsEquipos,
            codigoSolicitante // 🟢 EL NOVENO ARGUMENTO
        );

        // 4. Mostrar resultado
        if (exito) {
            JOptionPane.showMessageDialog(this, "✅ Préstamo y detalles registrados con éxito.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarCamposPrestamo(); // Asegúrate de tener este método para limpiar los campos
            cargarEquiposDisponibles(); // Recarga la tabla de activos disponibles
            // Limpia la tabla de detalle
            ((DefaultTableModel) jTableDetalle_Prestamo.getModel()).setRowCount(0);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Falló el registro del préstamo. Revise los logs del servidor para ver el error de la DB.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }

    } catch (NumberFormatException e) {
        logger.severe("Error al convertir ID de Monitor o Código: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "El ID Administrador/Monitor o Código de Solicitante no es un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        logger.severe("Error al registrar el préstamo: " + e.getMessage());
        JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

// Asegúrate de añadir este método auxiliar
private void limpiarCamposPrestamo() {
    jTextFieldCodigo_Solicitante.setText("");
    jTextFieldNombre_solicitante.setText("");
    jTextFieldCorreo.setText("");
    jTextFieldProposito_Prestamo.setText("");
    // Limpia la tabla de detalle
    modeloDetallePrestamo.setRowCount(0);
    }//GEN-LAST:event_jButtonGuardar_PrestamosActionPerformed
    private void limpiarCamposUsuario() {
    Id_cajadetexto.setText("");
    caja_texto_nombre.setText("");
    caja_texto_contraseña.setText("");
    caja_texto_nombre_completo.setText("");
    // Reiniciar el JComboBox del rol si es necesario
    // jComboBoxRol.setSelectedIndex(0); 
    
    // Habilitar/Deshabilitar botones si tienes lógica de estado
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Principal().setVisible(true));
    }
    public void cargarTabla(List<Activo> listaActivos) {
    // Obtenemos el modelo de la tabla para manipular sus filas
    DefaultTableModel modelo = (DefaultTableModel) Tabla_activos.getModel();
    
    // 1. Limpiar filas existentes (fundamental para la búsqueda y recarga)
    modelo.setRowCount(0);

    // 2. Llenar la tabla con los nuevos datos
    for (Activo activo : listaActivos) {
        // Creamos un array de Object con 7 elementos (coincidiendo con las columnas de tu tabla)
        Object[] fila = new Object[7]; 
        
        // Mapeamos los atributos del objeto Activo a las columnas de la tabla
        fila[0] = activo.getIdEquipo();
        fila[1] = activo.getNombre();
        fila[2] = activo.getMarca();
        fila[3] = activo.getModeloSerie();
        fila[4] = activo.getUbicacionActual(); // Ubicacion_actual
        fila[5] = activo.getEstado();
        fila[6] = activo.getFechaAdquisicion(); // Fecha_adquisicion (Java Date)
        
        modelo.addRow(fila);
    }
}

    /**
     * Método para establecer el texto de bienvenida en la cabecera.
     */
    public void setBienvenidaUsuario(String nombreUsuario) {
        // La etiqueta jLabel1 es la que contiene el texto de bienvenida.
        Etiqueta_de_Bienvenida.setText("Hola " + nombreUsuario + ", bienvenido al sistema de gestión de recursos audiovisuales.");
    }
    
    /**
     * Configura los listeners de clic para los paneles del menú para cambiar de pestaña.
     */
    private void setupNavigationListeners() {
        // Los índices se basan en el orden en que las pestañas fueron añadidas
        // a jTabbedPane1 en initComponents(): tab2 (0), tab4 (1), tab5 (2), tab1 (3), tab3 (4).
        
        // Mapeo sugerido (Asegúrate de que este orden corresponda a tus necesidades):
        // 0: jPanel2 (tab2)
        // 1: jPanel4 (tab4)
        // 2: jPanel5 (tab5)
        // 3: jPanel1 (tab1)
        // 4: jPanel3 (tab3)

        // Gestión de Activos/Equipos -> Pestaña 3 (Si es jPanel1)
        Gestion_equipos.addMouseListener(new TabMenuAdapter(3));
        
        // Gestión de Usuarios -> Pestaña 0 (Si es jPanel2)
        gestion_usuarios.addMouseListener(new TabMenuAdapter(0));
        
        // Registro de Préstamos -> Pestaña 4 (Si es jPanel3)
        Prestamo.addMouseListener(new TabMenuAdapter(4));
        
        // Registro de Devoluciones -> Pestaña 1 (Si es jPanel4)
        Devoluciones.addMouseListener(new TabMenuAdapter(1));
        
        // Reportes y Análisis -> Pestaña 2 (Si es jPanel5)
        Reportes_analisis.addMouseListener(new TabMenuAdapter(2));
        
    }
    
    /**
     * Clase interna simplificada para manejar el evento de clic de ratón en los paneles del menú.
     */
    private class TabMenuAdapter extends MouseAdapter {
        private final int tabIndex;

        public TabMenuAdapter(int tabIndex) {
            this.tabIndex = tabIndex;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Cambia la pestaña visible en el JTabbedPane
            jTabbedPane1.setSelectedIndex(tabIndex);
            
            // Lógica opcional para cambiar el color de fondo de los paneles del menú
            resetMenuColors();
            javax.swing.JPanel selectedPanel = (javax.swing.JPanel) e.getComponent();
            selectedPanel.setBackground(Color.WHITE); // Color para el panel seleccionado
        }
        
        // Opcional: Para mejorar la experiencia visual, resetea el color de todos
        // los paneles a su color original (o a un color de fondo un poco más claro/oscuro)
        private void resetMenuColors() {
            Color defaultColor = new Color(240, 240, 240); // Asume el color original de los JPanels
            Gestion_equipos.setBackground(defaultColor);
            gestion_usuarios.setBackground(defaultColor);
            Prestamo.setBackground(defaultColor);
            Devoluciones.setBackground(defaultColor);
            Reportes_analisis.setBackground(defaultColor);
            
            // Excepción: Puedes dejar los paneles del menú con color (153, 0, 0)
            // y solo cambiar el color del seleccionado a blanco si esa es la intención.
            // Si el color original de los paneles es blanco (como se ve en la imagen), 
            // no necesitas el reset, solo cambia el color de fondo a un color de "activo".
            // Para el ejemplo, asumimos que todos los paneles tienen fondo blanco inicialmente
            // y el cambio de color es solo para simular un "activo".
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Boton_cancelar;
    private javax.swing.JButton Boton_eliminar;
    private javax.swing.JButton Boton_guardar;
    private javax.swing.JButton Boton_nuevo;
    private javax.swing.JPanel Devoluciones;
    private javax.swing.JLabel Etiqueta_de_Bienvenida;
    private javax.swing.JPanel Gestion_equipos;
    private javax.swing.JTextField ID_Prestamo;
    private javax.swing.JTextField Id_cajadetexto;
    private javax.swing.JPanel Menu;
    private javax.swing.JPanel Prestamo;
    private javax.swing.JPanel Reportes_analisis;
    private javax.swing.JTable Tabla_activos;
    private javax.swing.JTextField Ubicacion_actual;
    private javax.swing.JButton boton_eliminar_Usuario;
    private javax.swing.JButton boton_guardar_usuario;
    private javax.swing.JButton boton_nuevo_Usuario;
    private javax.swing.JPasswordField caja_texto_contraseña;
    private javax.swing.JTextField caja_texto_nombre;
    private javax.swing.JTextField caja_texto_nombre_completo;
    private javax.swing.JComboBox<String> combo_box_Rol;
    private javax.swing.JPanel gestion_usuarios;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButtonAgregar_Equipo;
    private javax.swing.JButton jButtonBuscar;
    private javax.swing.JButton jButtonGestion_Equipos;
    private javax.swing.JButton jButtonGestion_Usuarios;
    private javax.swing.JButton jButtonGuardar_Prestamos;
    private javax.swing.JButton jButtonQuitar_Equipo;
    private javax.swing.JButton jButtonRegistrar_Equipo;
    private javax.swing.JButton jButtonRegistro_devolucion;
    private javax.swing.JButton jButtonRegistro_prestamos;
    private javax.swing.JButton jButtonReporte_PDF;
    private javax.swing.JButton jButtonReporte_analisis;
    private javax.swing.JButton jButtonreporte_excel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBoxEstado;
    private javax.swing.JComboBox<String> jComboBoxTipo_de_usuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBuscar;
    private javax.swing.JList<String> jListPanel_de_administracion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSpinner jSpinnerFecha_Prestamo;
    private javax.swing.JSpinner jSpinnerFecha_de_adquisicion;
    private javax.swing.JSpinner jSpinnerFecha_devolucion;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTableDetalle_Prestamo;
    private javax.swing.JTable jTablePrestamo;
    private javax.swing.JTable jTableUsuarios;
    private javax.swing.JTextArea jTextAreaObservaciones;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextFieldBuscar;
    private javax.swing.JTextField jTextFieldCodigo_Solicitante;
    private javax.swing.JTextField jTextFieldCorreo;
    private javax.swing.JTextField jTextFieldID;
    private javax.swing.JTextField jTextFieldID_Administrador;
    private javax.swing.JTextField jTextFieldMarca;
    private javax.swing.JTextField jTextFieldModelo;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldNombre_solicitante;
    private javax.swing.JTextField jTextFieldProposito_Prestamo;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    // End of variables declaration//GEN-END:variables
}
