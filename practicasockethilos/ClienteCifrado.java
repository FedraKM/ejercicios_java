package practicasockethilos;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * @author Fedra Macario
 */
public class ClienteCifrado{
    // Parámetros de conexión. "localhost" significa que se conecta a tu propia computadora.
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE CHAT Y CIFRADO INICIADO ===");

        try (
            // Intenta conectar el Socket al servidor. Si el servidor está apagado, lanza ConnectException.
            Socket socket = new Socket(HOST, PUERTO);
            // Herramienta para leer lo que manda el servidor.
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Herramienta para enviar texto al servidor.
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            // Teclado para capturar lo que el usuario tipea en consola.
            Scanner teclado = new Scanner(System.in)
        ) {
            
            // --- HILO RECEPTOR ---
            // Si no tuviera este hilo, el programa se pausaría en teclado.nextLine() esperando que
            // el usuario escriba. Mientras está pausado ahí, NO podría recibir mensajes de otros usuarios.
            // Para solucionarlo, se crea un hilo que trabaje de fondo SOLO leyendo mensajes que llegan.
            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    // Este bucle se queda eternamente escuchando.
                    while ((linea = entrada.readLine()) != null) {
                        // Imprime el mensaje que llegó del servidor.
                        System.out.println("\n" + linea);
                        // Vuelve a imprimir esto para mantener la interfaz prolija después de recibir algo.
                        System.out.print("Comando/Mensaje: "); 
                    }
                } catch (IOException e) {
                    System.out.println("\n[CLIENTE] Conexión cerrada con el servidor.");
                }
            });
            // Un "Daemon" es un hilo de baja prioridad.
            // setDaemon(true) le dice a Java: "Si el hilo principal (el main) termina, matá este hilo también".
            // Si fuera false, el programa nunca cerraría aunque pongas "SALIR", porque este hilo seguiría vivo esperando leer.
            receptor.setDaemon(true);
            receptor.start(); // Encendemos el hilo receptor.
            
            // --- HILO PRINCIPAL (Lectura de teclado) ---
            
            // Hace una micropausa de 100 milisegundos. 
            // Esto le da tiempo al Hilo Receptor a arrancar, recibir la pregunta del servidor ("Ingresá tu nombre")
            // e imprimirla en pantalla ANTES de que el programa pida el nextLine(). Es solo para estética visual.
            Thread.sleep(100); 
            
            // Bucle eterno del teclado.
            while (true) {
                // Se pausa acá hasta que se apriete Enter.
                String mensaje = teclado.nextLine();
                
                // Si apreta Enter sin escribir nada, ignora la vuelta y vuelve a esperar.
                if (mensaje.trim().isEmpty()) continue;
                
                // Envía el texto escrito al servidor.
                salida.println(mensaje);
                
                // Si escribe SALIR, rompe el bucle local, lo que lleva al fin del main()
                // y como consecuencia (por ser Daemon), mata al Hilo Receptor.
                if (mensaje.equalsIgnoreCase("SALIR")) {
                    break;
                }
            }

        } catch (ConnectException e) {
            System.err.println("[CLIENTE] No se encontró el servidor. ¿Está encendido?");
        } catch (Exception e) {
            System.err.println("[CLIENTE] Error: " + e.getMessage());
        }
    }  
}