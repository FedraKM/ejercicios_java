/**
 *
 * @author Fedra Macario
 */
package practicasocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Servidor {
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DE CIFRADO INICIADO EN PUERTO " + PUERTO + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("\n[SERVIDOR] Esperando conexión de un cliente...");
                Socket socket = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado desde: " + socket.getInetAddress());
                
                // Maneja la comunicación con el cliente conectado
                manejarCliente(socket);
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static void manejarCliente(Socket socket) {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            salida.println("Bienvenido al Servidor de Cifrado Prototipo!");
            salida.println("Comandos disponibles:");
            salida.println("  CRYPT <mensaje> -> Aplica el cifrado especial de reversa y sustitución");
            salida.println("  SALIR           -> cierra la conexión");
            salida.println("--------------------------------------------------");

            String mensajeRecibido;

            while ((mensajeRecibido = entrada.readLine()) != null) {
                System.out.println("[CLIENTE DICE]: " + mensajeRecibido);

                if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                    salida.println("Hasta luego! Cerrando conexión segura...");
                    System.out.println("[SERVIDOR] Cliente desconectado.");
                    break;
                }

                String respuesta = procesarMensaje(mensajeRecibido);
                salida.println(respuesta);
                System.out.println("[SERVIDOR RESPONDE]: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error en la comunicación: " + e.getMessage());
        }
    }

    private static String procesarMensaje(String mensaje) {
        // Divide en: [0] CRYPT, [1] El mensaje entero
        String[] partes = mensaje.trim().split("\\s+", 2);
        String comando = partes[0].toUpperCase();

        if (comando.equals("CRYPT")) {
            if (partes.length < 2 || partes[1].trim().isEmpty()) {
                return "ERROR: Debes escribir un texto. Ej: CRYPT programacion";
            }
            return "Cifrado generado: " + aplicarCifradoInventado(partes[1].trim());
        }

        
        return "Cifrado generado: " + aplicarCifradoInventado(mensaje.trim());
    }

    private static String aplicarCifradoInventado(String texto) {
        // 1. Invierte el texto (Espejo)
        String invertida = new StringBuilder(texto).reverse().toString();

        // 2. Sustitución de vocales por símbolos
        String resultado = invertida
                .replace("a", "@").replace("A", "@")
                .replace("e", "3").replace("E", "3")
                .replace("i", "!").replace("I", "!")
                .replace("o", "0").replace("O", "0")
                .replace("u", "v").replace("U", "v");

        // 3. Agrega el sello de seguridad final
        return resultado + "#SEC";
    }
}
