
package javaproyecto1;

public class Libro extends Material implements Prestable {
    private String autor;
    private boolean disponible;

    public Libro(String titulo, String autor) {
        super(titulo);
        this.autor = autor;
        this.disponible = true;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Libro: " + titulo + " - " + autor +
                (disponible ? " (Disponible)" : " (Prestado)"));
    }
    
     @Override
    public void prestar() {
        if (disponible) {
            disponible = false;
            System.out.println("Libro prestado");
        } else {
            System.out.println("No disponible");
        }
    }

    @Override
    public void devolver() {
        disponible = true;
        System.out.println("Libro devuelto");
    }
}
