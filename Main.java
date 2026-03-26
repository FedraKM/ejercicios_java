import java.util.Random;

public class Main {
  public static void main(String args[]) {
    Random random = new Random();
        
    int suma = 0;
    int cantidad = 500;
        
    for (int i = 0; i < cantidad; i++) {
        int numero = random.nextInt(1000 - 10 + 1) + 10;
        suma += numero;
    }
        
    double promedio = (double) suma / cantidad;
        
    System.out.println("Suma total: " + suma);
    System.out.println("Promedio: " + promedio);
  }
}
