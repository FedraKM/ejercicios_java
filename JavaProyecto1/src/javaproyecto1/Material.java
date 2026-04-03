
package javaproyecto1;

public abstract class Material {
    protected String titulo;
    protected static int contador = 0;

    public Material(String titulo) {
        this.titulo = titulo;
        contador ++;
    }
    
    public abstract void mostrarInfo();
    
    public static int getContador(){
        return contador;
    }
    
    
}
