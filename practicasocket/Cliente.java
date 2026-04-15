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

        try (
            Socket socket = new Socket(HOST, PUERTO);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            Scanner teclado = new Scanner(System.in)
        ) {
            System.out.println("Conexión establecida!\n");

            // 1. LEER BIENVENIDA: El servidor manda varias líneas al conectar.
            // Las leemos antes de entrar al bucle de escritura.
            String linea;
            for (int i = 0; i < 5; i++) { // Leemos las 5 líneas de bienvenida
                System.out.println("[SERVIDOR]: " + entrada.readLine());
            }

            // 2. BUCLE PING-PONG: Escribir -> Leer -> Repetir
            while (true) {
                System.out.print("Vos: ");
                String mensaje = teclado.nextLine();

                if (mensaje.trim().isEmpty()) continue;

                // Enviamos al servidor
                salida.println(mensaje);

                // Esperamos y leemos la respuesta del servidor (Bloqueante)
                String respuesta = entrada.readLine();
                System.out.println("[SERVIDOR]: " + respuesta);

                if (mensaje.equalsIgnoreCase("SALIR")) {
                    System.out.println("[CLIENTE] Cerrando sistema.");
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }
}
