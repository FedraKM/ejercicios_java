package PracticaSocket_Swing;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;



public class VentanaLogin extends javax.swing.JFrame {

    // ── Paleta de colores ──────────────────────────────────────
    private static final Color BG        = new Color(18, 18, 35);
    private static final Color CARD      = new Color(28, 28, 50);
    private static final Color VIOLETA   = new Color(99, 74, 219);
    private static final Color VIO_HOV   = new Color(119, 94, 239);
    private static final Color BORDE     = new Color(55, 55, 90);
    private static final Color INPUT_BG  = new Color(15, 15, 30);
    private static final Color TEXT_PRIM = new Color(230, 228, 255);
    private static final Color TEXT_MUT  = new Color(120, 118, 160);
    private static final Color VERDE     = new Color(52, 211, 153);
    private static final Color ROJO      = new Color(248, 113, 113);

    public VentanaLogin() {
        initComponents();
       aplicarEstilos();
    }

private void aplicarEstilos() {
    getContentPane().setBackground(BG);
    setTitle("Chat Multihilo");
    setResizable(false);

    // Usamos jPanel1 que es el que tenés en el diseño
    jPanel1.setBackground(CARD);
    jPanel1.setBorder(BorderFactory.createLineBorder(BORDE, 1));

    campNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    campNombre.setBackground(INPUT_BG);
    campNombre.setForeground(TEXT_PRIM);
    campNombre.setCaretColor(new Color(160, 150, 255));
    
    // --- Estilos para el campo de IP ---
    campIP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    campIP.setBackground(INPUT_BG);
    campIP.setForeground(TEXT_PRIM);
    campIP.setCaretColor(new Color(160, 150, 255));

    // Aplicamos el borde con margen interno para que el texto no toque las paredes
    campIP.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDE, 1),
        BorderFactory.createEmptyBorder(10, 14, 10, 14)));

    
    btnConectar.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnConectar.setBackground(VIOLETA);
    btnConectar.setForeground(Color.WHITE);

    lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
    lblEstado.setForeground(TEXT_MUT);

    // Enter en el campo también conecta
    campNombre.addActionListener(e -> intentarConexion());
    setRedondeado(btnConectar, 6);
    setRedondeado(campNombre, 4);
    setRedondeado(campIP, 4);
    
try {
    // Verificamos la ruta exacta
    java.net.URL url = getClass().getResource("/imagenes/icons8-charla.ezgif.com-effects(1).gif"); 
    
    if (url != null) {
        ImageIcon icon = new ImageIcon(url);
        // Lo ponemos un poco más grande para que se vea bien
        Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        
        lblIcono.setIcon(new ImageIcon(img));
        lblIcono.setText(""); // Borramos el texto
    }
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
}

private void intentarConexion() {
 String nombre = campNombre.getText().trim();
 // Capturamos y asignamos el valor de una sola vez
    String textoIp = campIP.getText().trim();
    final String ipServidor = textoIp.isEmpty() ? "localhost" : textoIp;

        if (nombre.isEmpty()) {
            lblEstado.setForeground(ROJO);
            lblEstado.setText(" El nombre no puede estar vacío.");
            campNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROJO, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            return;
        }

        campNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        btnConectar.setEnabled(false);
        btnConectar.setText("Conectando...");
        lblEstado.setForeground(VERDE);
        lblEstado.setText("Estableciendo conexión...");
        
        
        new Thread(() -> {
            try {
                //2. USAMOS LA VARIABLE EN LUGAR DE "localhost" FIJO
                Socket socket = new Socket(ipServidor, 5000);
            
                BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

                salida.println(nombre);

                SwingUtilities.invokeLater(() -> {
                    new VentanaChat(socket, entrada, salida, nombre).setVisible(true);
                    dispose();
                });

            } catch (ConnectException ex) {
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setForeground(ROJO);
                    lblEstado.setText(" No se pudo conectar. ¿Está el Servidor corriendo?");
                    btnConectar.setEnabled(true);
                    btnConectar.setText("CONECTAR");
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setForeground(ROJO);
                    lblEstado.setText("Error: " + ex.getMessage());
                    btnConectar.setEnabled(true);
                    btnConectar.setText("CONECTAR");
                });
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblIP = new javax.swing.JLabel();
        campNombre = new javax.swing.JTextField();
        btnConectar = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        lblSub = new javax.swing.JLabel();
        lblIcono = new javax.swing.JLabel();
        lblCampo = new javax.swing.JLabel();
        campIP = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(28, 28, 50));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("Chat multihilo");
        jPanel1.add(lblTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 184, 24));

        lblIP.setBackground(new java.awt.Color(28, 28, 50));
        lblIP.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblIP.setForeground(new java.awt.Color(120, 118, 160));
        lblIP.setText("IP del Servidor:");
        jPanel1.add(lblIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, -1, 20));

        campNombre.setBackground(new java.awt.Color(99, 74, 200));
        campNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campNombreActionPerformed(evt);
            }
        });
        jPanel1.add(campNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 152, -1));

        btnConectar.setBackground(new java.awt.Color(99, 74, 180));
        btnConectar.setForeground(new java.awt.Color(255, 255, 255));
        btnConectar.setText("CONECTAR");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });
        jPanel1.add(btnConectar, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 370, 120, 29));

        lblEstado.setBackground(new java.awt.Color(28, 28, 50));
        lblEstado.setForeground(new java.awt.Color(120, 118, 160));
        jPanel1.add(lblEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 370, 276, 30));

        lblSub.setBackground(new java.awt.Color(28, 28, 50));
        lblSub.setForeground(new java.awt.Color(120, 118, 160));
        lblSub.setText("Ingresá tu nombre para conectarte");
        jPanel1.add(lblSub, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, -1, -1));

        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-charla-ezgif.com-effects (1).gif"))); // NOI18N
        jPanel1.add(lblIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, -1, -1));

        lblCampo.setBackground(new java.awt.Color(28, 28, 50));
        lblCampo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCampo.setForeground(new java.awt.Color(120, 118, 160));
        lblCampo.setText("Nombre de usuario:");
        jPanel1.add(lblCampo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 20));

        campIP.setBackground(new java.awt.Color(99, 74, 200));
        campIP.setText("localhost");
        campIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campIPActionPerformed(evt);
            }
        });
        jPanel1.add(campIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 152, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        intentarConexion();

    }//GEN-LAST:event_btnConectarActionPerformed

    private void campIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campIPActionPerformed

    private void campNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campNombreActionPerformed

private void setRedondeado(JComponent componente, int radio) {
    componente.setBorder(new javax.swing.border.AbstractBorder() {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            // Esto hace que el borde no se vea "pixelado" (anti-aliasing)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(18, 18, 35)); // El color del fondo de la ventana para "recortar"
            
            // Dibujamos el arco
            g2d.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
        }
    });
}
 public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        java.util.logging.Logger.getLogger(VentanaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> {
        VentanaLogin v = new VentanaLogin();
        v.aplicarEstilos(); // No te olvides de llamar a esto para que se vea lindo
        v.setVisible(true);
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConectar;
    private javax.swing.JTextField campIP;
    private javax.swing.JTextField campNombre;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCampo;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblIP;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JLabel lblSub;
    private javax.swing.JLabel lblTitulo;
    // End of variables declaration//GEN-END:variables
}
