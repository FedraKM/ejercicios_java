
package practicaswing.swing.mvc;

import javax.swing.DefaultListModel;
import practicaswing.swing.mvc.controlador.ControladorTareas;
import practicaswing.swing.mvc.modelo.Tarea;
import practicaswing.swing.mvc.vista.VentanaTareas;

/**
 *
 * @author Fedra Macario
 */
public class Main {
    public static void main(String[] args) {
        // 1. Creamos el Modelo
        DefaultListModel<Tarea> modelo = new DefaultListModel<>();
        modelo.addElement(new Tarea("Realizar el trabajo práctico n°1"));

        // 2. Creamos la Vista y le pasamos el Modelo
        VentanaTareas vista = new VentanaTareas(modelo);

        // 3. Creamos el Controlador y le pasamos Vista y Modelo para que los una
        ControladorTareas controlador = new ControladorTareas(vista, modelo);

        // 4. Mostramos la ventana
        vista.setVisible(true);
    }
}
