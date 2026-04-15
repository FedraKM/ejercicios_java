/**
 *
 * @author Fedra Macario
 */
package practicasocket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
   private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE CIFRADO INICIADO ===");
        System.out.println("Conectando al servidor " + HOST + ":" + PUERTO + "...");

        try (
            Socket socket = new Socket(HOST, PUERTO);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!\n");

            // Hilo para leer del servidor en tiempo real
            Thread lectorServidor = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("\n[SERVIDOR]: " + respuesta);
                        System.out.print("Vos: ");
                    }
                } catch (IOException e) {
                    System.out.println("\n[CLIENTE] Conexión terminada.");
                }
            });

            lectorServidor.setDaemon(true);
            lectorServidor.start();

            Thread.sleep(300);

            while (true) {
                System.out.print("Vos: ");
                String mensaje = teclado.nextLine();

                if (mensaje.trim().isEmpty()) continue;

                salida.println(mensaje);

                if (mensaje.equalsIgnoreCase("SALIR")) {
                    Thread.sleep(300);
                    System.out.println("[CLIENTE] Cerrando sistema. ¡Hasta luego!");
                    break;
                }
                
                Thread.sleep(200); 
            }

        } catch (Exception e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }
}
