
package practicaswing.swing.mvc.controlador;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import practicaswing.swing.mvc.modelo.Tarea;
import practicaswing.swing.mvc.vista.VentanaTareas;

/**
 *
 * @author Fedra Macario
 */
public class ControladorTareas {
    private VentanaTareas vista;
    private DefaultListModel<Tarea> modelo;

    // El controlador necesita conocer la vista y el modelo para conectarlos
    public ControladorTareas(VentanaTareas vista, DefaultListModel<Tarea> modelo) {
        this.vista = vista;
        this.modelo = modelo;
        
        // Llamamos al método que vincula los clics
        inicializarEventos();
    }

    private void inicializarEventos() {
        // Acción 1: Agregar tarea
        ActionListener accionAgregar = e -> {
            String texto = vista.getTxtNuevaTarea().getText().trim();
            if (!texto.isEmpty()) {
                modelo.addElement(new Tarea(texto));
                vista.getTxtNuevaTarea().setText(""); 
            }
        };
        
        // Le pasamos la acción al botón y al campo de texto de la vista
        vista.getBtnAgregar().addActionListener(accionAgregar);
        vista.getTxtNuevaTarea().addActionListener(accionAgregar);

        // Acción 2: Tachar tarea con doble clic
        vista.getVistaLista().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = vista.getVistaLista().locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Tarea tareaSeleccionada = modelo.getElementAt(index);
                        tareaSeleccionada.cambiarEstado();
                        modelo.set(index, tareaSeleccionada);
                    }
                }
            }
        });
    }
}
