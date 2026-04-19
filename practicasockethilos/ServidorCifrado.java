package practicasockethilos;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Fedra Macario
 */

public class ServidorCifrado {
    // Puerto por donde el servidor escuchará a los clientes
    private static final int PUERTO = 5000;
    
    // Usamos CopyOnWriteArrayList porque es "Thread-Safe". 
    // Permite que un hilo recorra la lista mientras otro borra o agrega un cliente sin crashear.
    private static final List<ManejadorCliente> clientes = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DE CIFRADO MULTIHILO INICIADO ===");

        // El ServerSocket es el "oreja" del servidor, escucha en el puerto 5000
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                System.out.println("\n[SERVIDOR] Esperando conexión...");
                
                // accept() bloquea el programa hasta que llega un cliente.
                // Al llegar, devuelve un objeto Socket que es el "cable" de comunicación con ese cliente.
                Socket socket = serverSocket.accept(); 
                System.out.println("[SERVIDOR] Cliente conectado desde: " + socket.getInetAddress());
                
                // --- ARQUITECTURA MULTIHILO ---
                // Creamos un objeto que sabe cómo atender al cliente (ManejadorCliente)
                ManejadorCliente manejador = new ManejadorCliente(socket);
                
                // Lo metemos en un Thread (hilo) y lo arrancamos con .start()
                // Esto hace que el código de abajo corra en paralelo y el "while(true)" 
                // pueda volver arriba inmediatamente a esperar al SIGUIENTE cliente.
                new Thread(manejador).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error: " + e.getMessage());
        }
    }

    // Esta clase es el "trabajador". Cada instancia corre en su propio hilo.
    static class ManejadorCliente implements Runnable {
        private final Socket socket;   // La conexión física
        private PrintWriter salida;    // Para enviarle datos al cliente
        private String nombre;         // El nombre que elija el cliente

        ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Usamos try-with-resources para asegurar que los flujos se cierren al terminar
            try (
                // BufferedReader: Lee lo que el cliente escribe (texto plano)
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // PrintWriter: Envía texto al cliente (autoFlush=true envía el dato al instante)
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.salida = out;
                
                // Protocolo inicial: Pedir nombre
                salida.println("Ingresá tu nombre para iniciar:");
                nombre = entrada.readLine(); // Se detiene hasta que el cliente envía su nombre
                clientes.add(this);

                // Mensajes de bienvenida informativos
                salida.println("Bienvenido, " + nombre + "!");
                salida.println("Comandos: CRYPT <mensaje> | SALIR");
                salida.println("--------------------------------------------------");

                String mensajeRecibido;
                // Bucle de escucha: Mientras el cliente esté conectado y no mande null
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    System.out.println("[" + nombre + " DICE]: " + mensajeRecibido);

                    // Si el cliente quiere irse, rompemos el bucle
                    if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                        salida.println("Hasta luego! Cerrando conexión...");
                        break;
                    }

                    // Lógica de procesamiento (Cifrado)
                    String respuesta = procesarMensaje(mensajeRecibido);
                    salida.println(respuesta); // Le respondemos al cliente
                    System.out.println("[SERVIDOR RESPONDE A " + nombre + "]: " + respuesta);
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Comunicación interrumpida con " + nombre);
            } finally {
                // El bloque finally se ejecuta siempre, incluso si hay error.
                // Limpiamos la lista y cerramos el socket.
                clientes.remove(this);
                System.out.println("[-] " + nombre + " se desconectó.");
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        // Separa el comando de la palabra (Ej: "CRYPT Hola" -> "CRYPT" y "Hola")
        private String procesarMensaje(String mensaje) {
            String[] partes = mensaje.trim().split("\\s+", 2);
            String comando = partes[0].toUpperCase();

            if (comando.equals("CRYPT")) {
                if (partes.length < 2 || partes[1].trim().isEmpty()) {
                    return "ERROR: Debes escribir un texto después de CRYPT.";
                }
                return "Cifrado generado: " + aplicarCifradoInventado(partes[1].trim());
            }
            // Si no usa CRYPT, ciframos todo el mensaje por defecto
            return "Cifrado generado: " + aplicarCifradoInventado(mensaje.trim());
        }

        
        private String aplicarCifradoInventado(String texto) {
            // 1. Invertimos la cadena
            String invertida = new StringBuilder(texto).reverse().toString();
            // 2. Reemplazamos vocales por símbolos
            String resultado = invertida
                    .replace("a", "@").replace("A", "@")
                    .replace("e", "3").replace("E", "3")
                    .replace("i", "!").replace("I", "!")
                    .replace("o", "0").replace("O", "0")
                    .replace("u", "v").replace("U", "v");
            // 3. Firma final
            return resultado + "#SEC";
        }
    }  
}
