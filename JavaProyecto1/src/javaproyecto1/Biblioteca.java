
package javaproyecto1;
import java.util.ArrayList;

public class Biblioteca {
    private ArrayList<Material> materiales;

    public Biblioteca() {
        materiales = new ArrayList<>();
    }

    public void agregarMaterial(Material m) {
        materiales.add(m);
    }

    public void mostrarMateriales() {
        for (Material m : materiales) {
            m.mostrarInfo();
        }
    }
}
