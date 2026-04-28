package practicaswing.swing.mvc.modelo;

/**
 *
 * @author Fedra Macario
 */
public class Tarea {
    private String descripcion;
    private boolean completada;

    public Tarea(String descripcion) {
        this.descripcion = descripcion;
        this.completada = false;
    }

    public void cambiarEstado() {
        this.completada = !this.completada;
    }

    @Override
    public String toString() {
        if (completada) {
            return "<html><strike><font color='gray'>" + descripcion + "</font></strike></html>";
        }
        return descripcion;
    }
}
