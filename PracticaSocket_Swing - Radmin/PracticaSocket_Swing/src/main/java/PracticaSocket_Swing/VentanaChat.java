
package PracticaSocket_Swing;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class VentanaChat extends javax.swing.JFrame {


   // ── 1. AGREGÁS ESTAS VARIABLES (justo abajo del "public class") ──
    // Son las variables de red y el documento con colores
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String miNombre;
    private StyledDocument doc;
 
    public VentanaChat() {
        initComponents();
        aplicarEstilos();
    }

  // ── 3. AGREGÁS ESTE CONSTRUCTOR TUYO (abajo del anterior) ──
    // VentanaLogin va a llamar a este cuando conecte
    public VentanaChat(Socket socket, BufferedReader entrada, PrintWriter salida, String nombre) {
        initComponents();               // siempre primero
        aplicarEstilos();
        this.socket   = socket;
        this.entrada  = entrada;
        this.salida   = salida;
        this.miNombre = nombre;
 
        // Configuración inicial de la ventana
        setTitle("Chat — " + nombre);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        lblUsuario.setText(nombre);
        campMensaje.setText("");
        doc = areaChat.getStyledDocument();
 
        // Aplicar colores
        aplicarEstilos();
 
        // Cerrar limpiamente al hacer X
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                desconectar();
            }
        });
 
        // Arrancar el hilo que recibe mensajes del servidor
        iniciarReceptor();
    }
 
    // ── 4. MÉTODO TUYO: aplica colores a los componentes ──
    private void aplicarEstilos() {
        Color BG_DARK   = new Color(18, 18, 35);
        Color BG_PANEL  = new Color(22, 22, 42);
        Color VIOLETA   = new Color(99, 74, 219);
        Color BORDE     = new Color(55, 55, 90);
        Color INPUT_BG  = new Color(15, 15, 30);
        Color TEXT_PRIM = new Color(230, 228, 255);
        Color TEXT_MUT  = new Color(0, 168, 255);
        Color VERDE     = new Color(52, 211, 153);
 
        getContentPane().setBackground(BG_DARK);
        jPanel1.setBackground(BG_DARK);
 
        lblTitulo.setForeground(TEXT_PRIM);
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        lblTitulo.setBackground(BG_DARK);
 
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setBackground(VIOLETA);
        lblUsuario.setOpaque(true);
        lblUsuario.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 10, 3, 10));
        lblUsuario.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
 
        
        
        // Aplicamos los colores de fondo
        // Obligamos a que el componente sea opaco
        areaChat.setOpaque(true);
        areaChat.setBackground(BG_PANEL);
        areaChat.setForeground(TEXT_PRIM);
        areaChat.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
        areaChat.setEditable(false);
        
        // ── FIX NIMBUS: forzar el color de fondo en JTextPane ──
        javax.swing.UIDefaults defaults = new javax.swing.UIDefaults();
        defaults.put("TextPane.background", BG_PANEL);
        defaults.put("TextPane.opaque", Boolean.TRUE);
        areaChat.putClientProperty("Nimbus.Overrides", defaults);
        areaChat.putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
        
        scrollChat.getViewport().setBackground(BG_PANEL);
        scrollChat.setBorder(javax.swing.BorderFactory.createLineBorder(BORDE, 1));
 
        campMensaje.setBackground(INPUT_BG);
        campMensaje.setForeground(TEXT_PRIM);
        campMensaje.setCaretColor(Color.WHITE);
        campMensaje.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        campMensaje.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDE, 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
 
        btnEnviar.setBackground(VIOLETA);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
 
        // Chips de comandos
        estilizarChip(btnLista,   TEXT_MUT,  new Color(0, 30, 60));
        estilizarChip(btnHora,    TEXT_MUT,  new Color(0, 30, 60));
        estilizarChip(btnAll,     new Color(180, 160, 255), new Color(38, 32, 80));
        estilizarChip(btnPrivado, new Color(180, 160, 255), new Color(38, 32, 80));
        estilizarChip(btnBot,     new Color(94, 211, 243),  new Color(10, 35, 50));
        estilizarChip(btnSalir,   new Color(248, 113, 113), new Color(55, 15, 15));
 
        // Enter en el campo también envía
        campMensaje.addActionListener(e -> enviarMensaje());
        // Le damos aire al texto para que no toque los bordes del óvalo
    lblUsuario.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

    // Aplicamos la redondez (ajustá el 25 para que sea más o menos ovalado)
    setRedondeado(lblUsuario, 25);
    }
 
    private void estilizarChip(javax.swing.JButton btn, Color fg, Color bg) {
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(fg.darker(), 1),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btn.setFocusPainted(false);
        
        // Le damos aire al texto para que no toque los bordes del óvalo
lblUsuario.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

// Aplicamos la redondez (ajustá el 25 para que sea más o menos ovalado)
setRedondeado(lblUsuario, 15);
    }
 
    // ────────────────────────────────────────────────────────────
    //  initComponents — NO MODIFICAR, es el código del designer
    // ────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        scrollChat = new javax.swing.JScrollPane();
        areaChat = new javax.swing.JTextPane();
        campMensaje = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();
        btnLista = new javax.swing.JButton();
        btnHora = new javax.swing.JButton();
        btnAll = new javax.swing.JButton();
        btnPrivado = new javax.swing.JButton();
        btnBot = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(18, 18, 35));

        lblTitulo.setText("● Chat Multihilo");

        lblUsuario.setBackground(new java.awt.Color(99, 74, 219));
        lblUsuario.setForeground(new java.awt.Color(255, 255, 255));
        lblUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsuario.setOpaque(true);

        areaChat.setBackground(new java.awt.Color(28, 28, 50));
        scrollChat.setViewportView(areaChat);

        campMensaje.setText("jTextField1");

        btnEnviar.setText("▶");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        btnLista.setText("LISTA");
        btnLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListaActionPerformed(evt);
            }
        });

        btnHora.setText("HORA");
        btnHora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoraActionPerformed(evt);
            }
        });

        btnAll.setText("ALL");
        btnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllActionPerformed(evt);
            }
        });

        btnPrivado.setText("PRIVADO");
        btnPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrivadoActionPerformed(evt);
            }
        });

        btnBot.setText("BOT");
        btnBot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBotActionPerformed(evt);
            }
        });

        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btnLista)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHora)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrivado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir)
                        .addGap(18, 18, 18)
                        .addComponent(campMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                        .addGap(34, 34, 34)
                        .addComponent(btnEnviar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblTitulo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(scrollChat)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                    .addComponent(lblUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollChat, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLista)
                    .addComponent(btnHora)
                    .addComponent(btnAll)
                    .addComponent(btnPrivado)
                    .addComponent(btnBot)
                    .addComponent(btnSalir)
                    .addComponent(campMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviar))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
                       
 
    // ── 5. ACCIONES DE LOS BOTONES (NetBeans los generó, vos ponés el código) ──
    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        enviarMensaje();
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListaActionPerformed
        campMensaje.setText("LISTA");
        enviarMensaje();
    }//GEN-LAST:event_btnListaActionPerformed

    private void btnHoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoraActionPerformed
        campMensaje.setText("HORA");
        enviarMensaje();
    }//GEN-LAST:event_btnHoraActionPerformed

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllActionPerformed
        campMensaje.setText("*ALL ");
        campMensaje.requestFocus();
        campMensaje.setCaretPosition(campMensaje.getText().length());
    }//GEN-LAST:event_btnAllActionPerformed

    private void btnPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrivadoActionPerformed
        campMensaje.setText("*");
        campMensaje.requestFocus();
        campMensaje.setCaretPosition(campMensaje.getText().length());
    }//GEN-LAST:event_btnPrivadoActionPerformed

    private void btnBotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBotActionPerformed
        campMensaje.setText("BOT ");
        campMensaje.requestFocus();
        campMensaje.setCaretPosition(campMensaje.getText().length());
    }//GEN-LAST:event_btnBotActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        desconectar();
    }//GEN-LAST:event_btnSalirActionPerformed


     // ── 6. MÉTODOS TUYOS: lógica de red ──
 
    private void enviarMensaje() {
        String texto = campMensaje.getText().trim();
        if (texto.isEmpty()) return;
 
        agregarMensaje("Yo: " + texto, new Color(52, 211, 153));  // verde
        salida.println(texto);
        campMensaje.setText("");
        campMensaje.requestFocus();
 
        if (texto.equalsIgnoreCase("SALIR")) {
            desconectar();
        }
    }
 
    private void iniciarReceptor() {
        Thread t = new Thread(() -> {
            try {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    final String msg = linea;
                    
                    // --- NUEVA LÓGICA DE ACTUALIZACIÓN DE NOMBRE ---
                    if (msg.startsWith("[AVISO] El nombre ya existía")) {
                        // Extraemos el nombre que viene después de los dos puntos ":"
                        String nuevoNombre = msg.substring(msg.lastIndexOf(":") + 1).trim();

                        // Actualizamos todo en el hilo de la interfaz (Swing)
                        SwingUtilities.invokeLater(() -> {
                            this.miNombre = nuevoNombre; // Actualiza la variable interna
                            lblUsuario.setText(nuevoNombre); // Cambia el texto del chip
                            setTitle("Chat — " + nuevoNombre); // Cambia el título de la ventana
                        });
                    }
                    SwingUtilities.invokeLater(() -> agregarMensaje(msg, elegirColor(msg)));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    agregarMensaje("[Conexión cerrada]", new Color(248, 113, 113)));
            }
        });
        t.setDaemon(true);
        t.start();
    }
 
    private void desconectar() {
        try { salida.println("SALIR"); socket.close(); } catch (Exception ignored) {}
        dispose();
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
 
    // Agrega una línea con color al área de chat
    private void agregarMensaje(String texto, Color color) {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        StyleConstants.setFontFamily(attr, "Consolas");
        StyleConstants.setFontSize(attr, 13);
        try {
            doc.insertString(doc.getLength(), texto + "\n", attr);
            areaChat.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {}
    }
 
    // Elige el color según el tipo de mensaje
    private Color elegirColor(String msg) {
        if (msg.startsWith("[TODOS]"))   return new Color(250, 189, 47);   // amarillo
        if (msg.startsWith("[PRIVADO"))  return new Color(180, 160, 255);  // lila
        if (msg.startsWith("[BOT]"))     return new Color(94, 211, 243);   // cyan
        if (msg.startsWith("[ERROR]"))   return new Color(248, 113, 113);  // rojo
        if (msg.startsWith("[AVISO]"))   return new Color(250, 189, 47);   // amarillo
        if (msg.startsWith("---") || msg.startsWith("  ") || msg.startsWith(">"))
                                         return new Color(90, 88, 120);    // gris muted
        return new Color(230, 228, 255);                                   // blanco suave
    }
    // Método para crear un borde redondeado personalizado


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
            java.util.logging.Logger.getLogger(VentanaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

 try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaChat.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new VentanaChat().setVisible(true));
    }
    
    // Método para crear un borde redondeado personalizado
private void setRedondeado(JComponent componente, int radio) {
    componente.setBorder(new javax.swing.border.AbstractBorder() {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(55, 55, 90)); // Color del borde
            g2d.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
        }
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane areaChat;
    private javax.swing.JButton btnAll;
    private javax.swing.JButton btnBot;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnHora;
    private javax.swing.JButton btnLista;
    private javax.swing.JButton btnPrivado;
    private javax.swing.JButton btnSalir;
    private javax.swing.JTextField campMensaje;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JScrollPane scrollChat;
    // End of variables declaration//GEN-END:variables
}
