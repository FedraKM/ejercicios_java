
package javaproyecto1;

public class Revista extends Material implements Prestable {
    private int numero;
    private boolean disponible;

    public Revista(String titulo, int numero) {
        super(titulo);
        this.numero = numero;
        this.disponible = true;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("Revista: " + titulo + " N°" + numero +
                (disponible ? " (Disponible)" : " (Prestada)"));
    }

    @Override
    public void prestar() {
        disponible = false;
        System.out.println("Revista prestada");
    }

    @Override
    public void devolver() {
        disponible = true;
        System.out.println("Revista devuelta");
    }
}
