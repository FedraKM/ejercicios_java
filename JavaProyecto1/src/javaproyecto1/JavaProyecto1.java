
package javaproyecto1;

public class JavaProyecto1 {

    public static void main(String[] args) {
        
        Biblioteca b = new Biblioteca();

        Libro l1 = new Libro("1984", "Orwell");
        Revista r1 = new Revista("National Geographic", 202);

        b.agregarMaterial(l1);
        b.agregarMaterial(r1);

        b.mostrarMateriales();

        l1.prestar();
        r1.prestar();

        b.mostrarMateriales();

        System.out.println("Total materiales: " + Material.getContador());
    }  
}
