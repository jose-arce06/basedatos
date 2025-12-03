// IMPORTACIONES
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;

// CLASE PRINCIPAL
public class SistemaInventario extends JFrame {
    // VARIABLES DE LA APLICACIÓN
    private CardLayout cardsLayout;
    private JPanel cards;
    private CardLayout mainLayout;
    private JPanel mainPanel;

    // Componentes de interfaz para productos
    private JTable productosTable;
    private DefaultTableModel productosModel;
    private TableRowSorter<DefaultTableModel> productosSorter;
    private JTextField prodNombreField, prodPrecioField, prodCantidadField, prodDepartamentoField;
    private JComboBox<String> prodAlmacenComboBox;

    // Componentes de interfaz para almacenes
    private JTable almacenesTable;
    private DefaultTableModel almacenesModel;
    private TableRowSorter<DefaultTableModel> almacenesSorter;
    private JTextField almNombreField;

    // Conexión a base de datos
    private Connection connection;

    // CONTROL DE USUARIO Y ROLES
    private String usuarioActual;
    private String rolUsuarioActual;

    // MAPA DE ALMACENES
    private Map<String, Integer> mapaAlmacenes = new HashMap<>();

    // CONSTRUCTOR PRINCIPAL
    public SistemaInventario() {
        setTitle("Sistema de Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UiConstants.COLOR_BLANCO);
        try {
            connection = Database.conectarBD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        cardsLayout = new CardLayout();
        cards = new JPanel(cardsLayout);
        cards.setBackground(UiConstants.COLOR_BLANCO);
        cards.add(crearPanelLogin(), "login");
        add(cards);
        cardsLayout.show(cards, "login");
    }

    // CONTROL DE PERMISOS con mensaje
    private boolean tienePermiso(String operacion) {
        boolean ok = Permissions.tienePermiso(rolUsuarioActual, operacion);
        if (!ok) {
            String mensaje = "No tiene permisos para realizar esta operación.\n" +
                    "Rol actual: " + rolUsuarioActual + "\n" +
                    "Operación requerida: " + operacion;
            JOptionPane.showMessageDialog(this, mensaje, "Permiso Denegado", JOptionPane.WARNING_MESSAGE);
        }
        return ok;
    }

    // PANTALLA DE LOGIN (idéntica en apariencia)
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(40, 80, 40, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel perfilPanel = new JPanel(new BorderLayout());
        perfilPanel.setBackground(UiConstants.COLOR_BLANCO);
        perfilPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        try {
            ImageIcon perfilIcon = new ImageIcon("C:/Users/Home/Documents/Shool/Desarrollo de sistemas 2/intellij/basedatos/src/main/resources/perfil.png");
            Image image = perfilIcon.getImage();
            Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            perfilIcon = new ImageIcon(scaledImage);
            JLabel perfilLabel = new JLabel(perfilIcon);
            perfilLabel.setHorizontalAlignment(SwingConstants.CENTER);
            perfilPanel.add(perfilLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen de perfil: " + e.getMessage());
            JLabel placeholder = new JLabel("", SwingConstants.CENTER);
            placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 60));
            placeholder.setForeground(UiConstants.COLOR_AZUL);
            perfilPanel.add(placeholder, BorderLayout.CENTER);
        }

        JLabel titulo = new JLabel("Inicio de Sesión", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        titulo.setBorder(new EmptyBorder(0, 0, 25, 0));

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.BLACK);
        JTextField userField = UiComponents.crearCampoTextoLogin();
        userField.setPreferredSize(new Dimension(320, 50));

        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passLabel.setForeground(Color.BLACK);
        JPasswordField passField = UiComponents.crearCampoPasswordLogin();
        passField.setPreferredSize(new Dimension(320, 50));

        JButton loginBtn = new JButton("Iniciar sesión");
        loginBtn.setPreferredSize(new Dimension(320, 50));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginBtn.setBackground(UiConstants.COLOR_AZUL);
        loginBtn.setForeground(UiConstants.COLOR_BLANCO);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(perfilPanel, gbc);

        gbc.gridy = 1;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginBtn, gbc);

        gbc.gridy = 5; gbc.weighty = 1.0;
        panel.add(Box.createGlue(), gbc);

        loginBtn.addActionListener(e -> {
            String usuario = userField.getText().trim();
            String contrasena = new String(passField.getPassword());
            String rol = AuthService.validarUsuarioYObtenerRol(connection, usuario, contrasena);
            if (rol != null) {
                usuarioActual = usuario;
                rolUsuarioActual = rol;
                AuthService.actualizarUltimoInicio(connection, usuario);
                reconstruirInterfazPrincipal();
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    // RECONSTRUIR INTERFAZ PRINCIPAL
    private void reconstruirInterfazPrincipal() {
        if (mainPanel != null) cards.remove(mainPanel);
        mainLayout = new CardLayout();
        mainPanel = new JPanel(mainLayout);
        mainPanel.setBackground(UiConstants.COLOR_BLANCO);
        mainPanel.add(crearPanelInicio(), "inicio");
        mainPanel.add(crearPanelListaProductos(), "listaProductos");
        mainPanel.add(crearPanelFormularioProducto(), "formularioProducto");
        mainPanel.add(crearPanelListaAlmacenes(), "listaAlmacenes");
        mainPanel.add(crearPanelFormularioAlmacen(), "formularioAlmacen");
        cards.add(mainPanel, "main");
        cardsLayout.show(cards, "main");
        mainLayout.show(mainPanel, "inicio");
        crearMenu();
    }

    // MENÚ SUPERIOR
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UiConstants.COLOR_BLANCO);
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        userPanel.setBackground(UiConstants.COLOR_BLANCO);
        try {
            ImageIcon perfilIcon = new ImageIcon("C:/Users/Home/Documents/Shool/Desarrollo de sistemas 2/intellij/basedatos/src/main/resources/perfil.png");
            Image image = perfilIcon.getImage();
            Image scaledImage = image.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            perfilIcon = new ImageIcon(scaledImage);
            JLabel perfilLabel = new JLabel(perfilIcon);
            userPanel.add(perfilLabel);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen de perfil para el menú: " + e.getMessage());
        }
        JLabel userLabel = new JLabel(usuarioActual + " (" + rolUsuarioActual + ")");
        userLabel.setFont(UiConstants.FUENTE_BOLD);
        userLabel.setForeground(UiConstants.COLOR_AZUL);
        userPanel.add(userLabel);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userPanel);

        JMenu navMenu = new JMenu("Navegación");
        navMenu.setFont(UiConstants.FUENTE_BOLD);
        JMenuItem inicioItem = new JMenuItem("Inicio");
        JMenuItem productosItem = new JMenuItem("Productos");
        JMenuItem almacenesItem = new JMenuItem("Almacenes");
        JMenuItem cerrarItem = new JMenuItem("Cerrar sesión");
        navMenu.add(inicioItem);
        navMenu.add(productosItem);
        navMenu.add(almacenesItem);
        navMenu.addSeparator();
        navMenu.add(cerrarItem);
        menuBar.add(navMenu);
        setJMenuBar(menuBar);

        inicioItem.addActionListener(e -> mainLayout.show(mainPanel, "inicio"));
        productosItem.addActionListener(e -> {
            if (tienePermiso("VER_PRODUCTOS")) {
                cargarProductos();
                mainLayout.show(mainPanel, "listaProductos");
            }
        });
        almacenesItem.addActionListener(e -> {
            if (tienePermiso("VER_ALMACENES")) {
                cargarAlmacenes();
                mainLayout.show(mainPanel, "listaAlmacenes");
            }
        });
        cerrarItem.addActionListener(e -> {
            setJMenuBar(null);
            usuarioActual = null;
            rolUsuarioActual = null;
            cardsLayout.show(cards, "login");
        });
    }

    // PANEL DE INICIO
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titulo = new JLabel("Sistema de Gestión de Inventario", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        titulo.setBorder(new EmptyBorder(20, 0, 40, 0));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centroPanel = new JPanel(new BorderLayout());
        centroPanel.setBackground(UiConstants.COLOR_BLANCO);
        try {
            ImageIcon logoIcon = new ImageIcon("C:/Users/Home/Documents/Shool/Desarrollo de sistemas 2/intellij/basedatos/src/main/resources/LOGO.png");
            Image image = logoIcon.getImage();
            Image scaledImage = image.getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledImage);
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            centroPanel.add(logoLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.getMessage());
        }

        JLabel mensajeBienvenida = new JLabel("Bienvenido, " + usuarioActual + " (" + rolUsuarioActual + ") Desarrollado por: Jose Arce", SwingConstants.CENTER);
        mensajeBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mensajeBienvenida.setForeground(UiConstants.COLOR_AZUL);
        mensajeBienvenida.setBorder(new EmptyBorder(20, 0, 20, 0));
        centroPanel.add(mensajeBienvenida, BorderLayout.SOUTH);
        panel.add(centroPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(UiConstants.COLOR_BLANCO);
        infoPanel.setBorder(new CompoundBorder(new LineBorder(UiConstants.COLOR_AZUL, 1, true), new EmptyBorder(15, 15, 15, 15)));
        JLabel infoTitulo = new JLabel("Información del Usuario", SwingConstants.CENTER);
        infoTitulo.setFont(UiConstants.FUENTE_BOLD);
        infoTitulo.setForeground(UiConstants.COLOR_AZUL);
        infoPanel.add(infoTitulo, BorderLayout.NORTH);

        JTextArea permisosArea = new JTextArea();
        permisosArea.setEditable(false);
        permisosArea.setBackground(UiConstants.COLOR_BLANCO);
        permisosArea.setFont(UiConstants.FUENTE_BASE);
        permisosArea.setBorder(new EmptyBorder(10, 20, 10, 20));
        String permisos = "Permisos disponibles:\n\n";
        if ("ADMIN".equals(rolUsuarioActual)) {
            permisos += "• Gestión completa de productos\n• Gestión completa de almacenes\n• Todas las operaciones del sistema";
        } else if ("PRODUCTOS".equals(rolUsuarioActual)) {
            permisos += "• Ver productos\n• Agregar productos\n• Modificar productos\n• Eliminar productos";
        } else if ("ALMACENES".equals(rolUsuarioActual)) {
            permisos += "• Ver almacenes\n• Agregar almacenes\n• Eliminar almacenes";
        }
        permisosArea.setText(permisos);
        infoPanel.add(permisosArea, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        return panel;
    }

    // PANEL DE LISTA DE PRODUCTOS
    private JPanel crearPanelListaProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UiConstants.COLOR_BLANCO);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titulo = new JLabel("Lista de Productos", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        headerPanel.add(titulo, BorderLayout.WEST);

        JButton agregarBtn = UiComponents.crearBoton("Agregar Producto", UiConstants.COLOR_AZUL);
        agregarBtn.addActionListener(e -> {
            if (tienePermiso("AGREGAR_PRODUCTOS")) {
                limpiarCamposProducto();
                mainLayout.show(mainPanel, "formularioProducto");
            }
        });
        headerPanel.add(agregarBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel filtrosPanel = new JPanel();
        filtrosPanel.setLayout(new BoxLayout(filtrosPanel, BoxLayout.Y_AXIS));
        filtrosPanel.setBackground(UiConstants.COLOR_BLANCO);
        filtrosPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fila1.setBackground(UiConstants.COLOR_BLANCO);
        JLabel filtroGeneralLabel = new JLabel("Buscar General:");
        filtroGeneralLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroGeneralLabel.setForeground(UiConstants.COLOR_AZUL);
        fila1.add(filtroGeneralLabel);
        JTextField filtroProductosField = UiComponents.crearCampoTexto();
        filtroProductosField.setPreferredSize(new Dimension(200, 35));
        filtroProductosField.setToolTipText("Buscar por nombre, departamento o almacén");
        fila1.add(filtroProductosField);
        JLabel filtroPrecioLabel = new JLabel("Precio máximo:");
        filtroPrecioLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroPrecioLabel.setForeground(UiConstants.COLOR_AZUL);
        fila1.add(filtroPrecioLabel);
        JTextField filtroPrecioField = UiComponents.crearCampoTexto();
        filtroPrecioField.setPreferredSize(new Dimension(100, 35));
        filtroPrecioField.setToolTipText("Filtrar por precio máximo");
        fila1.add(filtroPrecioField);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fila2.setBackground(UiConstants.COLOR_BLANCO);
        JLabel filtroCantidadLabel = new JLabel("Cantidad mínima:");
        filtroCantidadLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroCantidadLabel.setForeground(UiConstants.COLOR_AZUL);
        fila2.add(filtroCantidadLabel);
        JTextField filtroCantidadField = UiComponents.crearCampoTexto();
        filtroCantidadField.setPreferredSize(new Dimension(100, 35));
        filtroCantidadField.setToolTipText("Filtrar por cantidad mínima en stock");
        fila2.add(filtroCantidadField);
        JLabel filtroDeptoLabel = new JLabel("Departamento:");
        filtroDeptoLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroDeptoLabel.setForeground(UiConstants.COLOR_AZUL);
        fila2.add(filtroDeptoLabel);
        JTextField filtroDeptoField = UiComponents.crearCampoTexto();
        filtroDeptoField.setPreferredSize(new Dimension(120, 35));
        filtroDeptoField.setToolTipText("Filtrar por departamento específico");
        fila2.add(filtroDeptoField);
        JLabel filtroAlmacenLabel = new JLabel("Almacén:");
        filtroAlmacenLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroAlmacenLabel.setForeground(UiConstants.COLOR_AZUL);
        fila2.add(filtroAlmacenLabel);
        JTextField filtroAlmacenField = UiComponents.crearCampoTexto();
        filtroAlmacenField.setPreferredSize(new Dimension(120, 35));
        filtroAlmacenField.setToolTipText("Filtrar por almacén específico");
        fila2.add(filtroAlmacenField);

        JPanel botonesFiltroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        botonesFiltroPanel.setBackground(UiConstants.COLOR_BLANCO);
        JButton aplicarFiltrosBtn = new JButton("Aplicar Filtros");
        aplicarFiltrosBtn.setFont(UiConstants.FUENTE_BASE);
        aplicarFiltrosBtn.setBackground(UiConstants.COLOR_VERDE);
        aplicarFiltrosBtn.setForeground(UiConstants.COLOR_BLANCO);
        aplicarFiltrosBtn.setFocusPainted(false);
        aplicarFiltrosBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JButton limpiarFiltroBtn = new JButton("Limpiar Filtros");
        limpiarFiltroBtn.setFont(UiConstants.FUENTE_BASE);
        limpiarFiltroBtn.setBackground(UiConstants.COLOR_DORADO);
        limpiarFiltroBtn.setForeground(UiConstants.COLOR_BLANCO);
        limpiarFiltroBtn.setFocusPainted(false);
        limpiarFiltroBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonesFiltroPanel.add(aplicarFiltrosBtn);
        botonesFiltroPanel.add(limpiarFiltroBtn);

        filtrosPanel.add(fila1);
        filtrosPanel.add(fila2);
        filtrosPanel.add(botonesFiltroPanel);

        JPanel centroPanel = new JPanel(new BorderLayout());
        centroPanel.setBackground(UiConstants.COLOR_BLANCO);
        centroPanel.add(filtrosPanel, BorderLayout.NORTH);

        productosModel = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Precio", "Cantidad", "Departamento", "Almacén",
                "Última Modificación", "Usuario Modificación"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productosTable = new JTable(productosModel);
        productosTable.setRowHeight(30);
        productosTable.setFont(UiConstants.FUENTE_BASE);
        productosTable.getTableHeader().setFont(UiConstants.FUENTE_BOLD);
        productosTable.getTableHeader().setBackground(UiConstants.COLOR_AZUL);
        productosTable.getTableHeader().setForeground(UiConstants.COLOR_BLANCO);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.removeColumn(productosTable.getColumnModel().getColumn(0));
        productosSorter = new TableRowSorter<>(productosModel);
        productosTable.setRowSorter(productosSorter);
        JScrollPane tableScroll = new JScrollPane(productosTable);
        centroPanel.add(tableScroll, BorderLayout.CENTER);
        panel.add(centroPanel, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonesPanel.setBackground(UiConstants.COLOR_BLANCO);
        botonesPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton modificarBtn = UiComponents.crearBoton("Modificar", UiConstants.COLOR_DORADO);
        JButton eliminarBtn = UiComponents.crearBoton("Eliminar", UiConstants.COLOR_ROJO);
        JButton actualizarBtn = UiComponents.crearBoton("Actualizar", UiConstants.COLOR_VERDE);
        botonesPanel.add(modificarBtn);
        botonesPanel.add(eliminarBtn);
        botonesPanel.add(actualizarBtn);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        aplicarFiltrosBtn.addActionListener(e -> aplicarFiltrosProductos(
                filtroProductosField.getText().trim(),
                filtroPrecioField.getText().trim(),
                filtroCantidadField.getText().trim(),
                filtroDeptoField.getText().trim(),
                filtroAlmacenField.getText().trim()
        ));
        limpiarFiltroBtn.addActionListener(e -> {
            filtroProductosField.setText("");
            filtroPrecioField.setText("");
            filtroCantidadField.setText("");
            filtroDeptoField.setText("");
            filtroAlmacenField.setText("");
            productosSorter.setRowFilter(null);
        });

        filtroProductosField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarGeneral(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarGeneral(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarGeneral(); }
            public void filtrarGeneral() {
                String texto = filtroProductosField.getText().trim();
                if (texto.isEmpty()) {
                    productosSorter.setRowFilter(null);
                } else {
                    productosSorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        modificarBtn.addActionListener(e -> {
            int fila = productosTable.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un producto para modificar.");
                return;
            }
            if (tienePermiso("MODIFICAR_PRODUCTOS")) {
                cargarDatosProductoSeleccionado();
                mainLayout.show(mainPanel, "formularioProducto");
            }
        });
        eliminarBtn.addActionListener(e -> {
            if (tienePermiso("ELIMINAR_PRODUCTOS")) {
                eliminarProducto();
            }
        });
        actualizarBtn.addActionListener(e -> cargarProductos());

        return panel;
    }

    // PANEL DE FORMULARIO DE PRODUCTO
    private JPanel crearPanelFormularioProducto() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UiConstants.COLOR_BLANCO);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titulo = new JLabel("Formulario de Producto", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        headerPanel.add(titulo, BorderLayout.WEST);
        JButton volverBtn = UiComponents.crearBoton("Volver a Lista", UiConstants.COLOR_AZUL);
        volverBtn.addActionListener(e -> {
            cargarProductos();
            mainLayout.show(mainPanel, "listaProductos");
        });
        headerPanel.add(volverBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel formularioPanel = new JPanel(new GridBagLayout());
        formularioPanel.setBackground(UiConstants.COLOR_BLANCO);
        formularioPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        formularioPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        prodNombreField = UiComponents.crearCampoTexto();
        formularioPanel.add(prodNombreField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END; gbc.weightx = 0.0;
        formularioPanel.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        prodPrecioField = UiComponents.crearCampoTexto();
        formularioPanel.add(prodPrecioField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END; gbc.weightx = 0.0;
        formularioPanel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        prodCantidadField = UiComponents.crearCampoTexto();
        formularioPanel.add(prodCantidadField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_END; gbc.weightx = 0.0;
        formularioPanel.add(new JLabel("Departamento:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        prodDepartamentoField = UiComponents.crearCampoTexto();
        formularioPanel.add(prodDepartamentoField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.LINE_END; gbc.weightx = 0.0;
        formularioPanel.add(new JLabel("Almacén:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        prodAlmacenComboBox = UiComponents.crearComboBox();
        formularioPanel.add(prodAlmacenComboBox, gbc);
        panel.add(formularioPanel, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonesPanel.setBackground(UiConstants.COLOR_BLANCO);
        botonesPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton guardarBtn = UiComponents.crearBoton("Guardar", UiConstants.COLOR_VERDE);
        JButton cancelarBtn = UiComponents.crearBoton("Cancelar", UiConstants.COLOR_ROJO);
        botonesPanel.add(guardarBtn);
        botonesPanel.add(cancelarBtn);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        cargarComboBoxAlmacenes();

        guardarBtn.addActionListener(e -> {
            if (productosTable.getSelectedRow() == -1) {
                if (tienePermiso("AGREGAR_PRODUCTOS")) {
                    agregarProducto();
                }
            } else {
                if (tienePermiso("MODIFICAR_PRODUCTOS")) {
                    modificarProducto();
                }
            }
        });
        cancelarBtn.addActionListener(e -> {
            cargarProductos();
            mainLayout.show(mainPanel, "listaProductos");
        });
        return panel;
    }

    // PANEL DE LISTA DE ALMACENES
    private JPanel crearPanelListaAlmacenes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UiConstants.COLOR_BLANCO);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titulo = new JLabel("Lista de Almacenes", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        headerPanel.add(titulo, BorderLayout.WEST);

        JButton agregarBtn = UiComponents.crearBoton("Agregar Almacén", UiConstants.COLOR_AZUL);
        agregarBtn.addActionListener(e -> {
            if (tienePermiso("AGREGAR_ALMACENES")) {
                almNombreField.setText("");
                mainLayout.show(mainPanel, "formularioAlmacen");
            }
        });
        headerPanel.add(agregarBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtrosPanel.setBackground(UiConstants.COLOR_BLANCO);
        filtrosPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel filtroLabel = new JLabel("Buscar:");
        filtroLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroLabel.setForeground(UiConstants.COLOR_AZUL);
        filtrosPanel.add(filtroLabel);
        JTextField filtroAlmacenesField = UiComponents.crearCampoTexto();
        filtroAlmacenesField.setPreferredSize(new Dimension(250, 35));
        filtroAlmacenesField.setToolTipText("Buscar por nombre de almacén");
        filtrosPanel.add(filtroAlmacenesField);
        JLabel filtroUsuarioLabel = new JLabel("Usuario modificación:");
        filtroUsuarioLabel.setFont(UiConstants.FUENTE_BOLD);
        filtroUsuarioLabel.setForeground(UiConstants.COLOR_AZUL);
        filtrosPanel.add(filtroUsuarioLabel);
        JTextField filtroUsuarioField = UiComponents.crearCampoTexto();
        filtroUsuarioField.setPreferredSize(new Dimension(150, 35));
        filtroUsuarioField.setToolTipText("Filtrar por usuario que modificó");
        filtrosPanel.add(filtroUsuarioField);

        JButton aplicarFiltrosAlmBtn = new JButton("Aplicar");
        aplicarFiltrosAlmBtn.setFont(UiConstants.FUENTE_BASE);
        aplicarFiltrosAlmBtn.setBackground(UiConstants.COLOR_VERDE);
        aplicarFiltrosAlmBtn.setForeground(UiConstants.COLOR_BLANCO);
        aplicarFiltrosAlmBtn.setFocusPainted(false);
        aplicarFiltrosAlmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JButton limpiarFiltroAlmBtn = new JButton("Limpiar");
        limpiarFiltroAlmBtn.setFont(UiConstants.FUENTE_BASE);
        limpiarFiltroAlmBtn.setBackground(UiConstants.COLOR_DORADO);
        limpiarFiltroAlmBtn.setForeground(UiConstants.COLOR_BLANCO);
        limpiarFiltroAlmBtn.setFocusPainted(false);
        limpiarFiltroAlmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filtrosPanel.add(aplicarFiltrosAlmBtn);
        filtrosPanel.add(limpiarFiltroAlmBtn);

        JPanel centroPanel = new JPanel(new BorderLayout());
        centroPanel.setBackground(UiConstants.COLOR_BLANCO);
        centroPanel.add(filtrosPanel, BorderLayout.NORTH);

        almacenesModel = new DefaultTableModel(new String[]{
                "ID", "Nombre", "Última Modificación", "Usuario Modificación"
        }, 0);
        almacenesTable = new JTable(almacenesModel);
        almacenesTable.setRowHeight(30);
        almacenesTable.setFont(UiConstants.FUENTE_BASE);
        almacenesTable.getTableHeader().setFont(UiConstants.FUENTE_BOLD);
        almacenesTable.getTableHeader().setBackground(UiConstants.COLOR_AZUL);
        almacenesTable.getTableHeader().setForeground(UiConstants.COLOR_BLANCO);
        almacenesTable.removeColumn(almacenesTable.getColumnModel().getColumn(0));
        almacenesSorter = new TableRowSorter<>(almacenesModel);
        almacenesTable.setRowSorter(almacenesSorter);
        JScrollPane tableScroll = new JScrollPane(almacenesTable);
        centroPanel.add(tableScroll, BorderLayout.CENTER);
        panel.add(centroPanel, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonesPanel.setBackground(UiConstants.COLOR_BLANCO);
        botonesPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton eliminarBtn = UiComponents.crearBoton("Eliminar", UiConstants.COLOR_ROJO);
        JButton actualizarBtn = UiComponents.crearBoton("Actualizar", UiConstants.COLOR_VERDE);
        botonesPanel.add(eliminarBtn);
        botonesPanel.add(actualizarBtn);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        aplicarFiltrosAlmBtn.addActionListener(e -> aplicarFiltrosAlmacenes(
                filtroAlmacenesField.getText().trim(),
                filtroUsuarioField.getText().trim()
        ));
        limpiarFiltroAlmBtn.addActionListener(e -> {
            filtroAlmacenesField.setText("");
            filtroUsuarioField.setText("");
            almacenesSorter.setRowFilter(null);
        });
        filtroAlmacenesField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarAlmacenesGeneral(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarAlmacenesGeneral(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarAlmacenesGeneral(); }
            public void filtrarAlmacenesGeneral() {
                String texto = filtroAlmacenesField.getText().trim();
                if (texto.isEmpty()) {
                    almacenesSorter.setRowFilter(null);
                } else {
                    almacenesSorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 1));
                }
            }
        });

        eliminarBtn.addActionListener(e -> {
            if (tienePermiso("ELIMINAR_ALMACENES")) {
                eliminarAlmacen();
            }
        });
        actualizarBtn.addActionListener(e -> cargarAlmacenes());
        return panel;
    }

    // PANEL DE FORMULARIO DE ALMACÉN
    private JPanel crearPanelFormularioAlmacen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UiConstants.COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UiConstants.COLOR_BLANCO);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titulo = new JLabel("Formulario de Almacén", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(UiConstants.COLOR_AZUL);
        headerPanel.add(titulo, BorderLayout.WEST);
        JButton volverBtn = UiComponents.crearBoton("Volver a Lista", UiConstants.COLOR_AZUL);
        volverBtn.addActionListener(e -> {
            cargarAlmacenes();
            mainLayout.show(mainPanel, "listaAlmacenes");
        });
        headerPanel.add(volverBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel formularioPanel = new JPanel(new GridBagLayout());
        formularioPanel.setBackground(UiConstants.COLOR_BLANCO);
        formularioPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_END;
        formularioPanel.add(new JLabel("Nombre del Almacén:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; gbc.weightx = 1.0;
        almNombreField = UiComponents.crearCampoTexto();
        formularioPanel.add(almNombreField, gbc);
        panel.add(formularioPanel, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonesPanel.setBackground(UiConstants.COLOR_BLANCO);
        botonesPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton guardarBtn = UiComponents.crearBoton("Guardar", UiConstants.COLOR_VERDE);
        JButton cancelarBtn = UiComponents.crearBoton("Cancelar", UiConstants.COLOR_ROJO);
        botonesPanel.add(guardarBtn);
        botonesPanel.add(cancelarBtn);
        panel.add(botonesPanel, BorderLayout.SOUTH);

        guardarBtn.addActionListener(e -> {
            if (tienePermiso("AGREGAR_ALMACENES")) {
                agregarAlmacen();
            }
        });
        cancelarBtn.addActionListener(e -> {
            cargarAlmacenes();
            mainLayout.show(mainPanel, "listaAlmacenes");
        });
        return panel;
    }

    // MÉTODOS DE FILTRO
    private void aplicarFiltrosProductos(String textoGeneral, String precioMax, String cantidadMin, String departamento, String almacen) {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!textoGeneral.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + textoGeneral));
        }
        if (!precioMax.isEmpty()) {
            try {
                double precio = Double.parseDouble(precioMax);
                filters.add(new RowFilter<Object, Object>() {
                    @Override
                    public boolean include(Entry<? extends Object, ? extends Object> entry) {
                        try {
                            String value = entry.getStringValue(2);
                            if (value == null || value.isEmpty()) return false;
                            double entryPrecio = Double.parseDouble(value);
                            return entryPrecio <= precio;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                });
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Formato de precio inválido");
            }
        }
        if (!cantidadMin.isEmpty()) {
            try {
                int cantidad = Integer.parseInt(cantidadMin);
                filters.add(new RowFilter<Object, Object>() {
                    @Override
                    public boolean include(Entry<? extends Object, ? extends Object> entry) {
                        try {
                            String value = entry.getStringValue(3);
                            if (value == null || value.isEmpty()) return false;
                            int entryCantidad = Integer.parseInt(value);
                            return entryCantidad >= cantidad;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                });
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Formato de cantidad inválido");
            }
        }
        if (!departamento.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + departamento, 4));
        }
        if (!almacen.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + almacen, 5));
        }
        if (!filters.isEmpty()) {
            productosSorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            productosSorter.setRowFilter(null);
        }
    }

    private void aplicarFiltrosAlmacenes(String nombre, String usuario) {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!nombre.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + nombre, 1));
        }
        if (!usuario.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + usuario, 3));
        }
        if (!filters.isEmpty()) {
            almacenesSorter.setRowFilter(RowFilter.andFilter(filters));
        } else {
            almacenesSorter.setRowFilter(null);
        }
    }

    // AUXILIARES
    private void cargarComboBoxAlmacenes() {
        if (prodAlmacenComboBox != null) {
            prodAlmacenComboBox.removeAllItems();
            mapaAlmacenes.clear();
            try {
                mapaAlmacenes.putAll(AlmacenDAO.obtenerMapaAlmacenes(connection));
                for (String nombre : mapaAlmacenes.keySet()) {
                    prodAlmacenComboBox.addItem(nombre);
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar almacenes: " + ex.getMessage());
            }
        }
    }

    // CRUD PRODUCTOS (delegando a DAO)
    private void cargarProductos() {
        try {
            ProductoDAO.cargarProductos(connection, productosModel);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void agregarProducto() {
        try {
            if (!validarCamposProducto()) return;
            String nombre = prodNombreField.getText().trim();
            double precio = Double.parseDouble(prodPrecioField.getText().trim());
            int cantidad = Integer.parseInt(prodCantidadField.getText().trim());
            String departamento = prodDepartamentoField.getText().trim();
            String nombreAlmacenSeleccionado = (String) prodAlmacenComboBox.getSelectedItem();
            int idAlmacen = mapaAlmacenes.get(nombreAlmacenSeleccionado);
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ProductoDAO.agregarProducto(connection, nombre, precio, cantidad, departamento, idAlmacen, fechaActual, usuarioActual);
            cargarProductos();
            limpiarCamposProducto();
            JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
            mainLayout.show(mainPanel, "listaProductos");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en formato de precio o cantidad");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void modificarProducto() {
        int fila = productosTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para modificar.");
            return;
        }
        try {
            if (!validarCamposProducto()) return;
            int id = (int) productosModel.getValueAt(fila, 0);
            String nombre = prodNombreField.getText().trim();
            double precio = Double.parseDouble(prodPrecioField.getText().trim());
            int cantidad = Integer.parseInt(prodCantidadField.getText().trim());
            String departamento = prodDepartamentoField.getText().trim();
            String nombreAlmacenSeleccionado = (String) prodAlmacenComboBox.getSelectedItem();
            int idAlmacen = mapaAlmacenes.get(nombreAlmacenSeleccionado);
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ProductoDAO.modificarProducto(connection, id, nombre, precio, cantidad, departamento, idAlmacen, fechaActual, usuarioActual);
            cargarProductos();
            limpiarCamposProducto();
            JOptionPane.showMessageDialog(this, "Producto modificado exitosamente.");
            mainLayout.show(mainPanel, "listaProductos");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en formato de precio o cantidad");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = productosTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este producto?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;
        try {
            int id = (int) productosModel.getValueAt(fila, 0);
            ProductoDAO.eliminarProducto(connection, id);
            cargarProductos();
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private boolean validarCamposProducto() {
        if (prodNombreField.getText().trim().isEmpty() ||
            prodPrecioField.getText().trim().isEmpty() ||
            prodCantidadField.getText().trim().isEmpty() ||
            prodDepartamentoField.getText().trim().isEmpty() ||
            prodAlmacenComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return false;
        }
        try {
            double precio = Double.parseDouble(prodPrecioField.getText().trim());
            int cantidad = Integer.parseInt(prodCantidadField.getText().trim());
            if (precio < 0 || cantidad < 0) {
                JOptionPane.showMessageDialog(this, "Precio y cantidad deben ser valores positivos");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio y cantidad deben ser números válidos");
            return false;
        }
        return true;
    }

    private void limpiarCamposProducto() {
        prodNombreField.setText("");
        prodPrecioField.setText("");
        prodCantidadField.setText("");
        prodDepartamentoField.setText("");
        if (prodAlmacenComboBox.getItemCount() > 0) {
            prodAlmacenComboBox.setSelectedIndex(0);
        }
    }

    private void cargarDatosProductoSeleccionado() {
        int fila = productosTable.getSelectedRow();
        if (fila != -1) {
            prodNombreField.setText(productosModel.getValueAt(fila, 1).toString());
            prodPrecioField.setText(productosModel.getValueAt(fila, 2).toString());
            prodCantidadField.setText(productosModel.getValueAt(fila, 3).toString());
            prodDepartamentoField.setText(productosModel.getValueAt(fila, 4).toString());
            String almacenNombre = productosModel.getValueAt(fila, 5).toString();
            prodAlmacenComboBox.setSelectedItem(almacenNombre);
        }
    }

    // CRUD ALMACENES (delegando a DAO)
    private void cargarAlmacenes() {
        try {
            AlmacenDAO.cargarAlmacenes(connection, almacenesModel);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void agregarAlmacen() {
        try {
            if (almNombreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del almacén es obligatorio");
                return;
            }
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            AlmacenDAO.agregarAlmacen(connection, almNombreField.getText().trim(), fechaActual, usuarioActual);
            cargarAlmacenes();
            cargarComboBoxAlmacenes();
            almNombreField.setText("");
            JOptionPane.showMessageDialog(this, "Almacén agregado.");
            mainLayout.show(mainPanel, "listaAlmacenes");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void eliminarAlmacen() {
        int fila = almacenesTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un almacén para eliminar.");
            return;
        }
        int idAlmacen = (int) almacenesModel.getValueAt(fila, 0);
        String nombreAlmacen = (String) almacenesModel.getValueAt(fila, 1);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el almacén '" + nombreAlmacen + "'?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;
        try {
            if (AlmacenDAO.tieneProductos(connection, idAlmacen)) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar el almacén porque tiene productos asociados.");
                return;
            }
            AlmacenDAO.eliminarAlmacen(connection, idAlmacen);
            cargarAlmacenes();
            cargarComboBoxAlmacenes();
            JOptionPane.showMessageDialog(this, "Almacén eliminado.");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SistemaInventario().setVisible(true));
    }
}
