package GestionarBBDDconJframe;

import MapeoPojos.Estudiante;
import MapeoPojos.Universidad;
import Util.NewHibernateUtil;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Ventana principal de la aplicacion GestionUniversidad-CRUD. Implementa el
 * patron MVC con Hibernate como capa de persistencia.
 *
 * @author ACDEZIN
 */
public class JDialog extends javax.swing.JDialog {

    /**
     * Constructor de la ventana. Inicializa los componentes de la interfaz.
     */
    public JDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // redimensionar la pantalla 
        //        Dimension tamanoPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        //        this.setBounds(0, 0, tamanoPantalla.width, tamanoPantalla.height);
        //        this.setResizable(true);
        //        this.setVisible(true);
    }

    // ============================================================
    // METODOS CRUD - UNIVERSIDADES
    // ============================================================
    /**
     * Inserta una nueva universidad en la base de datos. Recoge los datos del
     * formulario, los valida y los persiste con Hibernate.
     */
    public void anadirUniversidadConMetodo() {

        String codigoStr = txtCodigo.getText();
        String nombre = txtUniversidad.getText();
        String provincia = txtProvincia.getText();
        boolean esPrivada = boxSi.isSelected();

        if (codigoStr.isEmpty() || nombre.isEmpty() || provincia.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos."); // si estan vacios mostramos una ventana JOptioPane

        } else {
            int codigo = Integer.parseInt(codigoStr);

            JOptionPane.showMessageDialog(this, "Registro guardado con éxito.", "Correcto", JOptionPane.INFORMATION_MESSAGE);

            // 1  Abrir una sesión de Hibernate
            Session sesion = NewHibernateUtil.getSessionFactory().openSession();

            //2 Crear nuevo Objeto de la de la clase nuevaUniversidad
            Universidad nuevaUniversidad = new Universidad();
            nuevaUniversidad.setCodigo(codigo);
            nuevaUniversidad.setNombre(nombre);
            nuevaUniversidad.setProvinciaUni(provincia);
            nuevaUniversidad.setPrivada(esPrivada);

            // 3 Iniciamos la Transaccion
            Transaction transaccion = null;

            try {
                // Iniciar la transacción
                transaccion = sesion.beginTransaction();

                //4 Guardar el objeto Universidad en la base de datos
                sesion.save(nuevaUniversidad);

                //5 Confirmar la transacción (guardar los cambios en la base de datos)
                transaccion.commit();

                JOptionPane.showMessageDialog(null, "Universidad añadida correctamente.");
            } catch (Exception ex) {

                // Si ocurre un error, hacer rollback
                if (transaccion != null) {
                    transaccion.rollback();
                }
                ex.printStackTrace();
            } finally {
                // Cerrar la sesión de Hibernate
                sesion.close();
            }

        }
        // Limpiar los Campos JtextField
        txtCodigo.setText("");
        txtUniversidad.setText("");
        txtProvincia.setText("");
        boxSi.setSelected(false);
    }

    // ============================================================
    // METODOS CRUD - ESTUDIANTES
    // ============================================================
    /**
     * Inserta un nuevo estudiante en la base de datos. Valida todos los campos
     * y establece la relacion Many-to-One con Universidad.
     */
    public void insertarEstudiante() throws ParseException {

        String nif = txtNif.getText();
        String nombreEstu = txtNombreEstu.getText();
        String apellidos = txtApellidos.getText();
        String fechaNac = txtFechaNac.getText();
        String direccion = txtDireccion.getText();
        String provinciaEstu = txtProvinciaEstu.getText();
        String importeMat = txtImporteMat.getText();
        String becado = txtBecado.getText();
        String codigoUni = txtCodigoUni.getText();

        if (nif.isEmpty() || nombreEstu.isEmpty() || apellidos.isEmpty()
                || fechaNac.isEmpty() || direccion.isEmpty() || provinciaEstu.isEmpty()
                || importeMat.isEmpty() || becado.isEmpty() || codigoUni.isEmpty()) {

            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
        } else {
            Session sesion = NewHibernateUtil.getSessionFactory().openSession();

            float importeMatFloat = Float.parseFloat(importeMat);

            boolean becadoConversion = Boolean.parseBoolean(becado);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date fechaNacimiento = dateFormat.parse(fechaNac);

            int codigoUniConversion = Integer.parseInt(codigoUni);

            Universidad insertarUniversidad = (Universidad) sesion.get(Universidad.class, codigoUniConversion); // sesion.get retorna un Objecto de tipo Universidad y luego tenemos que hacer un casting a Universidad     

            Estudiante estudiantePrimero = new Estudiante();
            estudiantePrimero.setNif(nif);
            estudiantePrimero.setNombre(nombreEstu);
            estudiantePrimero.setApellidos(apellidos);
            estudiantePrimero.setFechaNacimiento(fechaNacimiento);
            estudiantePrimero.setDireccion(direccion);
            estudiantePrimero.setProvincia(provinciaEstu);
            estudiantePrimero.setImporteMatricula(importeMatFloat);
            estudiantePrimero.setBecado(becadoConversion);
            if (insertarUniversidad == null) {
                JOptionPane.showMessageDialog(null, "No existe una universidad con el código proporcionado.");

            } else {
                estudiantePrimero.setUniversidad(insertarUniversidad);
            }

            Transaction transaccion = null;

            try {

                transaccion = sesion.beginTransaction();

                sesion.save(estudiantePrimero);

                transaccion.commit();
                JOptionPane.showMessageDialog(null, "Datos ingresados correctamente.");

            } catch (Exception e) {
                if (transaccion != null) {
                    transaccion.rollback();
                }
                e.printStackTrace();
            } finally {
                sesion.close();
            }

        }
    }

    /**
     * Modifica el importe de matricula de un estudiante por su NIF.
     */
    public void modificarImporteMatricula() throws ParseException {

        String nif = txtNifModificar.getText();
        String nuevoImporteMat = txtNuevoImporteMat.getText();

        if (nif.isEmpty() || nuevoImporteMat.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");

        } else {

            float nuevoImporteMatFloat = Float.parseFloat(nuevoImporteMat);

            Session sesion = NewHibernateUtil.getSessionFactory().openSession();
            Transaction transaccion = null;

            try {

                transaccion = sesion.beginTransaction();

                Estudiante estudianteModificar = (Estudiante) sesion.get(Estudiante.class, nif);

                if (estudianteModificar != null) {

                    estudianteModificar.setImporteMatricula(nuevoImporteMatFloat);

                    sesion.saveOrUpdate(estudianteModificar);

                    transaccion.commit();

                    JOptionPane.showMessageDialog(null, "Importe de matrícula modificado correctamente.");
                } else {

                    JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
                }

            } catch (Exception e) {

                if (transaccion != null) {
                    transaccion.rollback();
                }
                e.printStackTrace();
            } finally {

                sesion.close();
            }
        }
    }

    /**
     * Elimina universidad de la base de datos por su Codigo.
     */
    public void borrarUniversidadPK() {

        String codigoStr = txtCodigoUniversidadBorrar.getText();
        int codigo = Integer.parseInt(codigoStr);

        Session sesion = NewHibernateUtil.getSessionFactory().openSession(); // Llamamos al método getSessionFactory() de la clase NewHibernateUtil (que configura Hibernate) y abrimos la sesión que interactúa con la BBDD.

        Transaction transaccion = null;

        try {

            transaccion = sesion.beginTransaction();

            Universidad universidadBorrar = (Universidad) sesion.get(Universidad.class, codigo);

            if (universidadBorrar != null) {

                if (universidadBorrar.getEstudiantes() != null && !universidadBorrar.getEstudiantes().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No se puede eliminar la universidad porque tiene estudiantes matriculados.");
                } else {

                    sesion.delete(universidadBorrar);
                    transaccion.commit();
                    JOptionPane.showMessageDialog(null, "Universidad borrada correctamente.");
                }

            } else {

                JOptionPane.showMessageDialog(null, "No se encontro la universidad");
            }

        } catch (Exception e) {

            if (transaccion != null) {
                transaccion.rollback();
            }
            e.printStackTrace();
        } finally {

            sesion.close();
        }

        txtCodigoUniversidadBorrar.setText("");
    }

    /**
     * Elimina un estudiante de la base de datos por su NIF.
     */
    public void borrarEstudiantes() {

        String recogeNif = txtNifEstudiante.getText();

        Session aperturaSesion = NewHibernateUtil.getSessionFactory().openSession();

        Transaction transaccion = null;

        try {

            transaccion = aperturaSesion.beginTransaction();

            Estudiante estudianteBorrar = (Estudiante) aperturaSesion.get(Estudiante.class, recogeNif);

            if (estudianteBorrar != null) {
                aperturaSesion.delete(estudianteBorrar);

                transaccion.commit();
                JOptionPane.showMessageDialog(null, "Estudiante borrado correctamente.");

            } else {

                JOptionPane.showMessageDialog(null, "No se encontro  Estudiante");
            }

        } catch (Exception e) {

            if (transaccion != null) {
                transaccion.rollback();
            }
            e.printStackTrace();
        } finally {

            aperturaSesion.close();
        }

    }

    // ============================================================
    // METODOS DE LISTADO (READ)
    // ============================================================
    /**
     * Lista todas las universidades en la tabla de la interfaz grafica.
     */
    public void listarEstudiantes() {

        Session sesionMostrarDatos = null; // abrimos aqui para poder meterlo en el finally del catch para cerrarla( y siempre se ejecute esa orden ) 
        try {
            sesionMostrarDatos = NewHibernateUtil.getSessionFactory().openSession();

            String hql = "FROM Estudiante";
            Query consulta = sesionMostrarDatos.createQuery(hql);

            List<Estudiante> ListadoEstudiantes = consulta.list();

            DefaultTableModel modelo = (DefaultTableModel) tbEstudiante.getModel();

            modelo.setRowCount(0);

            for (Estudiante iterarEstudiantes : ListadoEstudiantes) { // Leer PASO 4X ( explicacion )

                Hibernate.initialize(iterarEstudiantes.getUniversidad());
                Object[] fila = new Object[9];
                fila[0] = iterarEstudiantes.getNif();
                fila[1] = iterarEstudiantes.getNombre();
                fila[2] = iterarEstudiantes.getApellidos();
                fila[3] = iterarEstudiantes.getFechaNacimiento();
                fila[4] = iterarEstudiantes.getDireccion();
                fila[5] = iterarEstudiantes.getProvincia();
                fila[6] = iterarEstudiantes.getImporteMatricula();

                if (iterarEstudiantes.getBecado()) {
                    fila[7] = "Sí";
                } else {
                    fila[7] = "No";
                }
                fila[8] = iterarEstudiantes.getUniversidad().getCodigo();
                modelo.addRow(fila);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sesionMostrarDatos != null) {
                sesionMostrarDatos.close();
            }
        }

    }

    /**
     * Lista todos los estudiantes en la tabla de la interfaz grafica. Incluye
     * la informacion de la universidad asociada (relacion Many-to-One).
     */
    public void listarUniversidades() {

        Session sesion = null;
        try {

            sesion = NewHibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Universidad";
            Query consulta = sesion.createQuery(hql);
            List<Universidad> listaResultados = consulta.list();

            DefaultTableModel modelo = (DefaultTableModel) tbConsulta.getModel();
            modelo.setRowCount(0);

            for (Universidad universidad : listaResultados) {
                Object[] fila = new Object[4];
                fila[0] = universidad.getCodigo();
                fila[1] = universidad.getNombre();
                fila[2] = universidad.getProvinciaUni();

                if (universidad.getPrivada()) {
                    fila[3] = "Sí";
                } else {
                    fila[3] = "No";
                }

                modelo.addRow(fila);
            }

        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (sesion != null) {
                sesion.close();
            }
        }
    }

  // ============================================================
    // CONSULTAS AVANZADAS CON HQL
    // ============================================================
    /**
     * Consulta A: Nombre, apellidos, universidad e importe de matricula.
     * Ordenados por importe de menor a mayor.
     */
    public void consultaA() { // Nombre, apellidos, universidad e importe de matrícula de los estudiantes, ordenados por importe de la matrícula de menor a mayor.

        Session sesionConsulta = null;

        try {

            sesionConsulta = NewHibernateUtil.getSessionFactory().openSession();

            String consultaHQL = "SELECT e.nombre, e.apellidos, e.universidad.codigo, e.importeMatricula\n"
                    + "FROM Estudiante e\n" + // IMPORTANTE SE USA EL NOMBRE DE LA CLASE ( lo que seria la tabla )
                    "ORDER BY e.importeMatricula ASC";

            Query consulta = sesionConsulta.createQuery(consultaHQL);

            List<Object[]> ListadoEstudiantes = consulta.list();

            StringBuilder resultado = new StringBuilder();

            for (Object[] fila : ListadoEstudiantes) {
                String nombre = (String) fila[0];
                String apellidos = (String) fila[1];
                Integer codigoUniversidad = (Integer) fila[2];
                Float importeMatricula = (Float) fila[3];

                resultado.append("Nombre: ").append(nombre).append("\n");
                resultado.append("Apellidos: ").append(apellidos).append("\n");
                resultado.append("Código Universidad: ").append(codigoUniversidad).append("\n");
                resultado.append("Importe Matricula: ").append(importeMatricula).append("\n");
                resultado.append("--------------------------\n");
            }

            jtextResultadoConsulta.setText(resultado.toString());

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (sesionConsulta != null) {
                sesionConsulta.close();
            }
        }

    }

    /**
     * Consulta B: Nombre de universidad y total ingresado por matriculas.
     * Agrupa por universidad y suma los importes.
     */
    public void consultaB() {
        Session sesionConsulta = null;

        try {

            sesionConsulta = NewHibernateUtil.getSessionFactory().openSession();

            String consultaHQL = "SELECT u.nombre, SUM(e.importeMatricula) "
                    + "FROM Universidad u "
                    + "JOIN u.estudiantes e "
                    + "GROUP BY u.nombre";

            Query consulta = sesionConsulta.createQuery(consultaHQL);

            List<Object[]> ListadoEstudiantes = consulta.list();

            StringBuilder resultado = new StringBuilder();

            for (Object[] fila : ListadoEstudiantes) {
                String nombre = (String) fila[0];
                Double importeMatricula = (Double) fila[1];

                resultado.append("Nombre Universidad: ").append(nombre).append("\n");
                resultado.append("Total Importe: ").append(importeMatricula).append("\n");

                resultado.append("--------------------------\n");
            }

            jtextResultadoConsulta.setText(resultado.toString());
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (sesionConsulta != null) {
                sesionConsulta.close();
            }
        }

    }

    /**
     * Consulta C: Nombre, apellidos y direccion de estudiantes becados de
     * Almeria.
     */
    private void consultaC() {
        Session sesionConsulta = null;

        try {

            sesionConsulta = NewHibernateUtil.getSessionFactory().openSession();

            String consultaHQL = "SELECT e.nombre, e.apellidos, e.direccion "
                    + "FROM Estudiante e "
                    + "WHERE e.provincia = 'ALMERIA' AND e.becado = 0";

            Query consulta = sesionConsulta.createQuery(consultaHQL);

            List<Object[]> ListadoEstudiantes = consulta.list();

            StringBuilder resultado = new StringBuilder();

            for (Object[] fila : ListadoEstudiantes) {
                String nombre = (String) fila[0];
                String apellidos = (String) fila[1];
                String direccion = (String) fila[2];

                resultado.append("Nombre: ").append(nombre).append("\n");
                resultado.append("Apellidos: ").append(apellidos).append("\n");
                resultado.append("Direccion: ").append(direccion).append("\n");

                resultado.append("--------------------------\n");
            }

            jtextResultadoConsulta.setText(resultado.toString());
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (sesionConsulta != null) {
                sesionConsulta.close();
            }
        }

    }

    // ============================================================
    // METODOS DE VALIDACION
    // ============================================================
    /**
     * Valida el formato del NIF espanol: 8 numeros + 1 letra mayuscula.
     */
    private void validarNif(KeyEvent evt, JTextField textoJtextfield, int maxLongitud) {
        String texto = textoJtextfield.getText();

        if (texto.length() >= maxLongitud) {
            evt.consume();
            JOptionPane.showMessageDialog(null, "El texto no puede ser superorior a" + maxLongitud + " caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        char caracter = evt.getKeyChar();
        String conversorCaracter = String.valueOf(caracter);

        if (texto.length() < 8) {
            if (!conversorCaracter.matches("[0-9]") && caracter != KeyEvent.VK_BACK_SPACE && caracter != KeyEvent.VK_ENTER) {
                evt.consume();
                JOptionPane.showMessageDialog(null, "Solo se permiten números en los primeros 8 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (texto.length() == 8) {
            if (!conversorCaracter.matches("[A-Z]") && caracter != KeyEvent.VK_BACK_SPACE && caracter != KeyEvent.VK_ENTER) {
                evt.consume();
                JOptionPane.showMessageDialog(null, "El Ultimo carácter debe de ser una letra mayúscula.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnListadoUniversidades = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbConsulta = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtextResultadoConsulta = new javax.swing.JTextArea();
        btnConsultaA = new javax.swing.JButton();
        btnConsultaB = new javax.swing.JButton();
        btnConsultaC = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnListarEstudiantes = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbEstudiante = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnInsertarEstudiantes = new javax.swing.JButton();
        etiNif = new javax.swing.JLabel();
        etiNombreEstu = new javax.swing.JLabel();
        etiApellidos = new javax.swing.JLabel();
        etiFechaNac = new javax.swing.JLabel();
        etiDireccion = new javax.swing.JLabel();
        etiProvinciaEstu = new javax.swing.JLabel();
        etiImporteMat = new javax.swing.JLabel();
        etiBecado = new javax.swing.JLabel();
        etiCodigoUni = new javax.swing.JLabel();
        txtNif = new javax.swing.JTextField();
        txtNombreEstu = new javax.swing.JTextField();
        txtApellidos = new javax.swing.JTextField();
        txtFechaNac = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtProvinciaEstu = new javax.swing.JTextField();
        txtImporteMat = new javax.swing.JTextField();
        txtBecado = new javax.swing.JTextField();
        txtCodigoUni = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnBorrarEstudiante = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtNifEstudiante = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnModificarEstidiante = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtNifModificar = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNuevoImporteMat = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnAnadirConMetodo = new javax.swing.JButton();
        etiPrivada = new javax.swing.JLabel();
        boxSi = new javax.swing.JCheckBox();
        txtProvincia = new javax.swing.JTextField();
        etiProvincia = new javax.swing.JLabel();
        etiNombreUni = new javax.swing.JLabel();
        etiNombre = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtUniversidad = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        btnBorrar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtCodigoUniversidadBorrar = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Listado Universidades"));

        btnListadoUniversidades.setText("Listado ");
        btnListadoUniversidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListadoUniversidadesActionPerformed(evt);
            }
        });

        tbConsulta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Provincia Universidad", "Privada "
            }
        ));
        jScrollPane1.setViewportView(tbConsulta);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnListadoUniversidades)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnListadoUniversidades)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(278, 278, 278))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Consultas"));

        jtextResultadoConsulta.setColumns(20);
        jtextResultadoConsulta.setRows(5);
        jScrollPane4.setViewportView(jtextResultadoConsulta);

        btnConsultaA.setText("Consulta A");
        btnConsultaA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultaAActionPerformed(evt);
            }
        });

        btnConsultaB.setText("Consulta B");
        btnConsultaB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultaBActionPerformed(evt);
            }
        });

        btnConsultaC.setText("Consulta C");
        btnConsultaC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultaCActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnConsultaB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConsultaC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConsultaA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 913, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnConsultaA)
                        .addGap(33, 33, 33)
                        .addComponent(btnConsultaB)
                        .addGap(40, 40, 40)
                        .addComponent(btnConsultaC)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Listado de Estudiantes"));

        btnListarEstudiantes.setText("Listar Estudiantes");
        btnListarEstudiantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListarEstudiantesActionPerformed(evt);
            }
        });

        tbEstudiante.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "NIF", "Nombre", "Apellidos", "Fecha Nacimiento", "Direccion", "Provincia", "Importe Matricula", "Becado", "Codigo Universidad"
            }
        ));
        jScrollPane2.setViewportView(tbEstudiante);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnListarEstudiantes)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnListarEstudiantes)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir nuevos Estudiantes"));

        btnInsertarEstudiantes.setText("Insertar Estudiantes");
        btnInsertarEstudiantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarEstudiantesActionPerformed(evt);
            }
        });

        etiNif.setText("NIF");

        etiNombreEstu.setText("Nombre");

        etiApellidos.setText("Apellidos");

        etiFechaNac.setText("Fecha Nacimiento");

        etiDireccion.setText("Dirección");

        etiProvinciaEstu.setText("Provincia");

        etiImporteMat.setText("Importe matricula");

        etiBecado.setText("Becado");

        etiCodigoUni.setText("Codigo universidad");

        txtNif.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNifKeyTyped(evt);
            }
        });

        txtBecado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBecadoActionPerformed(evt);
            }
        });

        jLabel1.setText("True(Si) / False(No)");

        jLabel8.setText("Formato dd-mm-yyyy");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etiNombreEstu)
                            .addComponent(etiNif))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNif, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtNombreEstu, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(etiApellidos)))
                        .addGap(18, 18, 18)
                        .addComponent(txtApellidos))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etiImporteMat)
                            .addComponent(etiBecado)
                            .addComponent(etiCodigoUni)
                            .addComponent(btnInsertarEstudiantes)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(etiFechaNac)
                                    .addComponent(etiProvinciaEstu)
                                    .addComponent(etiDireccion))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8))
                                    .addComponent(txtProvinciaEstu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(txtCodigoUni, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtBecado, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtImporteMat, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(18, 18, 18)
                                            .addComponent(jLabel1))))))
                        .addGap(0, 42, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiNif)
                    .addComponent(txtNif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiNombreEstu)
                    .addComponent(txtNombreEstu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiApellidos)
                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiFechaNac)
                    .addComponent(txtFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProvinciaEstu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiProvinciaEstu))
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiDireccion)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiImporteMat)
                    .addComponent(txtImporteMat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBecado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(etiBecado)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiCodigoUni)
                    .addComponent(txtCodigoUni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnInsertarEstudiantes)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Borrar Estudiantes de la Base de Datos"));

        btnBorrarEstudiante.setText("Borrar");
        btnBorrarEstudiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarEstudianteActionPerformed(evt);
            }
        });

        jLabel4.setText("Seleccione el NIF del estudiante que desea borrar");

        txtNifEstudiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNifEstudianteActionPerformed(evt);
            }
        });
        txtNifEstudiante.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNifEstudianteKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtNifEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBorrarEstudiante))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNifEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnBorrarEstudiante)
                .addGap(63, 63, 63))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Modificar datos de estudiante"));

        btnModificarEstidiante.setText("Modificar");
        btnModificarEstidiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarEstidianteActionPerformed(evt);
            }
        });

        jLabel6.setText("Introduzca el NIF del estudiante que desea modificar");

        txtNifModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNifModificarActionPerformed(evt);
            }
        });
        txtNifModificar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNifModificarKeyTyped(evt);
            }
        });

        jLabel7.setText("Introduzca el nuevo importe de matricula");

        txtNuevoImporteMat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNuevoImporteMatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtNifModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(txtNuevoImporteMat)))
                    .addComponent(btnModificarEstidiante))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNifModificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtNuevoImporteMat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(btnModificarEstidiante)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir Nueva Universidad"));

        btnAnadirConMetodo.setText("Insertar Universidad");
        btnAnadirConMetodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirConMetodoActionPerformed(evt);
            }
        });

        etiPrivada.setText("Privada");

        boxSi.setText(" Si");
        boxSi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxSiActionPerformed(evt);
            }
        });

        etiProvincia.setText("Provincia");

        etiNombreUni.setText("Nombre Universidad");

        etiNombre.setText("Codigo");

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoKeyTyped(evt);
            }
        });

        jLabel9.setText("(No seleccionar si es Publica)");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAnadirConMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etiNombre)
                            .addComponent(etiNombreUni)
                            .addComponent(etiProvincia)
                            .addComponent(etiPrivada))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boxSi)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtUniversidad, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiNombre)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiNombreUni)
                    .addComponent(txtUniversidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiProvincia)
                    .addComponent(txtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiPrivada)
                    .addComponent(boxSi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(btnAnadirConMetodo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Borrar Universidad"));

        btnBorrar.setText("Borrar Universidad");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        jLabel10.setText("Seleccione el Codigo de la universidad que desea borrar");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBorrar)
                    .addComponent(jLabel10)
                    .addComponent(txtCodigoUniversidadBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 82, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCodigoUniversidadBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnBorrar)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(2670, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(393, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1462, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 443, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInsertarEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarEstudiantesActionPerformed
        try {
            // TODO add your handling code here:
            insertarEstudiante();
            // Limpiar todos los JTextField después de hacer la inserción
            txtNif.setText("");
            txtNombreEstu.setText("");
            txtApellidos.setText("");
            txtFechaNac.setText("");
            txtDireccion.setText("");
            txtProvinciaEstu.setText("");
            txtImporteMat.setText("");
            txtBecado.setText("");
            txtCodigoUni.setText("");
        } catch (ParseException ex) {
            Logger.getLogger(JDialog.class.getName()).log(Level.SEVERE, null, ex);
            // Mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "Error al insertar el estudiante: " + ex.getMessage());
        }


    }//GEN-LAST:event_btnInsertarEstudiantesActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void boxSiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxSiActionPerformed
        // TODO add your handling code here:
        // Hacer algo cuando el JCheckBox cambia, lo pones aquí
        if (boxSi.isSelected()) {
            System.out.println("La universidad será privada");
        } else {
            System.out.println("La universidad será pública");
        }

    }//GEN-LAST:event_boxSiActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed

        // usar metodo borrar
        borrarUniversidadPK();

    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnAnadirConMetodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirConMetodoActionPerformed
        // TODO add your handling code here:
        anadirUniversidadConMetodo();
    }//GEN-LAST:event_btnAnadirConMetodoActionPerformed

    private void txtNifEstudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNifEstudianteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNifEstudianteActionPerformed

    private void btnBorrarEstudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarEstudianteActionPerformed
        // TODO add your handling code here:
        borrarEstudiantes();
        txtNifEstudiante.setText("");
    }//GEN-LAST:event_btnBorrarEstudianteActionPerformed

    private void btnListarEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListarEstudiantesActionPerformed
        // TODO add your handling code here:
        listarEstudiantes();
    }//GEN-LAST:event_btnListarEstudiantesActionPerformed

    private void btnConsultaAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultaAActionPerformed
        // TODO add your handling code here:
        //consultaA(); esta es la consulta que tengo comentada 
        consultaA();
    }//GEN-LAST:event_btnConsultaAActionPerformed

    private void btnConsultaBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultaBActionPerformed
        // TODO add your handling code here:
        consultaB();
    }//GEN-LAST:event_btnConsultaBActionPerformed

    private void btnConsultaCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultaCActionPerformed
        // TODO add your handling code here:
        consultaC();
    }//GEN-LAST:event_btnConsultaCActionPerformed

    private void btnModificarEstidianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarEstidianteActionPerformed
        try {
            // TODO add your handling code here:
            modificarImporteMatricula();
            txtNifModificar.setText("");
            txtNuevoImporteMat.setText("");
        } catch (ParseException ex) {
            Logger.getLogger(JDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnModificarEstidianteActionPerformed

    private void txtNifModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNifModificarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNifModificarActionPerformed

    private void txtNuevoImporteMatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNuevoImporteMatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNuevoImporteMatActionPerformed

    // ============================================================
    // METODOS DE VALIDACION
    // ============================================================
    /**
     * Valida el campo de codigo de universidad en tiempo real. Solo permite
     * numeros y limita a un maximo de 5 digitos.
     */
    private void txtCodigoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyTyped

        String validarCodigo = txtCodigo.getText();

        if (validarCodigo.length() >= 5) {

            evt.consume();

            JOptionPane.showMessageDialog(null, "El texto debe tener un máximo de 5 digitos.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {

            char caracter = evt.getKeyChar();

            String conversorCaracter = String.valueOf(caracter);

            if (!conversorCaracter.matches("[0-9]") && caracter != KeyEvent.VK_BACK_SPACE && caracter != KeyEvent.VK_ENTER) {// KeyEvent 

                evt.consume();
                JOptionPane.showMessageDialog(null, "El texto debe contener solo numeros .", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_txtCodigoKeyTyped

    // ============================================================
    // METODOS DE VALIDACION
    // ============================================================
    /**
     * Valida el campo NIF en tiempo real mientras el usuario escribe. Formato
     * requerido: 8 numeros + 1 letra mayuscula (ej: 12345678A).
     */
    private void txtNifKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNifKeyTyped
        // TODO add your handling code here:
        // EVENTO PARA VALIDAR DNI cuando se escribe en el Jtexfield

        String validarNif = txtNif.getText();

        if (validarNif.length() >= 9) {

            evt.consume();

            JOptionPane.showMessageDialog(null, "El texto debe 8 numeros y 1 letra.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {

            char caracter = evt.getKeyChar();

            String conversorCaracter = String.valueOf(caracter);

            if (validarNif.length() < 8) {
                if (!conversorCaracter.matches("[0-9]") && caracter != KeyEvent.VK_BACK_SPACE && caracter != KeyEvent.VK_ENTER) {

                    evt.consume();
                    JOptionPane.showMessageDialog(null, "Solo se permiten números en los primeros 8 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (validarNif.length() == 8) {
                if (!conversorCaracter.matches("[A-Z]") && caracter != KeyEvent.VK_BACK_SPACE && caracter != KeyEvent.VK_ENTER) {

                    evt.consume();
                    JOptionPane.showMessageDialog(null, "El último carácter debe ser una letra mayúscula.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


    }//GEN-LAST:event_txtNifKeyTyped

    private void btnListadoUniversidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListadoUniversidadesActionPerformed
        // TODO add your handling code here:
        listarUniversidades();
    }//GEN-LAST:event_btnListadoUniversidadesActionPerformed

    private void txtNifEstudianteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNifEstudianteKeyTyped
        // TODO add your handling code here:

        // USAMOS EL METODO PARA VALIDAR EL ESTUDIANTE ( Cambia ligeramente ya que hay que pasarle los parametros evt, el nombre del jtextfield y en mi caso el numero de caracteres)
        validarNif(evt, txtNifEstudiante, 9);
    }//GEN-LAST:event_txtNifEstudianteKeyTyped

    private void txtNifModificarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNifModificarKeyTyped
        // TODO add your handling code here:
        validarNif(evt, txtNifModificar, 9);
    }//GEN-LAST:event_txtNifModificarKeyTyped

    private void txtBecadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBecadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBecadoActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog dialog = new JDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox boxSi;
    private javax.swing.JButton btnAnadirConMetodo;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnBorrarEstudiante;
    private javax.swing.JButton btnConsultaA;
    private javax.swing.JButton btnConsultaB;
    private javax.swing.JButton btnConsultaC;
    private javax.swing.JButton btnInsertarEstudiantes;
    private javax.swing.JButton btnListadoUniversidades;
    private javax.swing.JButton btnListarEstudiantes;
    private javax.swing.JButton btnModificarEstidiante;
    private javax.swing.JLabel etiApellidos;
    private javax.swing.JLabel etiBecado;
    private javax.swing.JLabel etiCodigoUni;
    private javax.swing.JLabel etiDireccion;
    private javax.swing.JLabel etiFechaNac;
    private javax.swing.JLabel etiImporteMat;
    private javax.swing.JLabel etiNif;
    private javax.swing.JLabel etiNombre;
    private javax.swing.JLabel etiNombreEstu;
    private javax.swing.JLabel etiNombreUni;
    private javax.swing.JLabel etiPrivada;
    private javax.swing.JLabel etiProvincia;
    private javax.swing.JLabel etiProvinciaEstu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jtextResultadoConsulta;
    private javax.swing.JTable tbConsulta;
    private javax.swing.JTable tbEstudiante;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtBecado;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCodigoUni;
    private javax.swing.JTextField txtCodigoUniversidadBorrar;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtFechaNac;
    private javax.swing.JTextField txtImporteMat;
    private javax.swing.JTextField txtNif;
    private javax.swing.JTextField txtNifEstudiante;
    private javax.swing.JTextField txtNifModificar;
    private javax.swing.JTextField txtNombreEstu;
    private javax.swing.JTextField txtNuevoImporteMat;
    private javax.swing.JTextField txtProvincia;
    private javax.swing.JTextField txtProvinciaEstu;
    private javax.swing.JTextField txtUniversidad;
    // End of variables declaration//GEN-END:variables
}
