package practicasockethilos;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *
 * @author Fedra Macario
 */
public class ClienteCifrado {
    private static final String HOST = "localhost"; // IP del servidor (local)
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE CIFRADO INICIADO ===");

        try (
            Socket socket = new Socket(HOST, PUERTO);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!");

            // --- HILO RECEPTOR (Thread paralelo) ---
            // Este hilo solo tiene un trabajo: escuchar lo que el servidor mande e imprimirlo.
            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    // readLine() se queda esperando a que el servidor mande algo
                    while ((linea = entrada.readLine()) != null) {
                        System.out.println("\n[SERVIDOR]: " + linea);
                        System.out.print("Vos: "); // Vuelve a mostrar el prompt para que sea cómodo escribir
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENTE] El hilo receptor se detuvo (conexión cerrada).");
                }
            });
            // Marcamos el hilo como Daemon para que se cierre automáticamente al cerrar el programa
            receptor.setDaemon(true);
            receptor.start();
            
            // --- HILO PRINCIPAL (Lectura de teclado) ---
            // Mientras el hilo de arriba escucha, este se encarga de enviar.
            while (true) {
                String mensaje = teclado.nextLine();
                if (mensaje.trim().isEmpty()) continue;
                
                salida.println(mensaje); // Enviamos el texto al servidor
                
                // Si el usuario escribe SALIR, cerramos el bucle y termina el main()
                if (mensaje.equalsIgnoreCase("SALIR")) {
                    System.out.println("[CLIENTE] Finalizando sesión...");
                    break;
                }
            }

        } catch (ConnectException e) {
            System.err.println("[CLIENTE] No se encontró el servidor. ¿Está encendido?");
        } catch (IOException e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }  
}
