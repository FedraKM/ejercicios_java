package practicasockethilos;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fedra Macario
 */
public class ServidorCifrado {
    // Define el puerto de red por el cual el servidor va a "escuchar" las conexiones entrantes.
    private static final int PUERTO = 5000;
    
    // Usa ConcurrentHashMap porque como tiene múltiples hilos (clientes) conectándose y desconectándose al mismo tiempo,
    // un hilo podría intentar leer la lista mientras otro la está modificando, causando un error fatal.
    // ConcurrentHashMap es "Thread-Safe", maneja estos choques automáticamente de forma segura.
    // Guarda el "Nombre del usuario" (String) apuntando a su "Hilo/Manejador" (ManejadorCliente).
    private static final Map<String, ManejadorCliente> clientesConectados = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("[LOG] === SERVIDOR MULTIHILO INICIADO EN PUERTO " + PUERTO + " ===");

        // ServerSocket abre el puerto y se queda esperando.
        // El try-with-resources asegura que el puerto se libere si el programa crashea.
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            
            // Este bucle infinito es el "Corazón del Servidor". Su único trabajo es aceptar gente.
            while (true) {
                System.out.println("\n[LOG] Esperando conexión...");
                
                // .accept() pausa el hilo principal aquí mismo hasta que un cliente toca la puerta.
                // Cuando un cliente se conecta, devuelve un objeto 'Socket' (el cable de comunicación).
                Socket socket = serverSocket.accept(); 
                System.out.println("[LOG] Cliente conectado desde: " + socket.getInetAddress());
                
                // ARQUITECTURA MULTIHILO:
                // Si se atiende al cliente aquí, el bucle no podría avanzar para aceptar a un segundo cliente.
                // Por eso, se crea un nuevo objeto ManejadorCliente (que tiene las instrucciones),
                // se mete adentro de un nuevo Thread (Hilo), y se le da .start().
                // Ahora ese cliente corre en paralelo, y el bucle vuelve arriba a esperar al siguiente.
                new Thread(new ManejadorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("[LOG] Error: " + e.getMessage());
        }
    }

    // Runnable es una interfaz de Java que significa "Esta clase tiene código que puede correr en un Hilo".
    // Obliga a implementar el método public void run()
    static class ManejadorCliente implements Runnable {
        private final Socket socket;         // La conexión física con ESTE cliente específico.
        private PrintWriter salida;          // La herramienta para enviarle texto al cliente.
        private String nombreUsuario;        // El nombre definitivo que usará este cliente.

        // Constructor: Recibe el socket en el momento que el cliente entra.
        ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        // Este es el código que se ejecuta en paralelo en el nuevo hilo.
        @Override
        public void run() {
            try (
                // entrada: lee lo que llega por el cable desde el cliente.
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // out: escribe texto hacia el cable. 'true' es el autoFlush, envía el mensaje instantáneamente.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                this.salida = out; // Guarda en el PrintWriter para poder usarlo desde otros métodos.
                
                // --- 1. PROTOCOLO DE CONEXIÓN Y NOMBRES ÚNICOS ---
                salida.println("Ingresá tu nombre para iniciar:");
                String nombreElegido = entrada.readLine(); 
                if (nombreElegido == null) return; // Prevención: si el cliente cerró la ventana de golpe.
                
                // Llama a la función que verifica si el nombre ya existe y le agrega un número si hace falta.
                this.nombreUsuario = asignarNombreUnico(nombreElegido.trim());
                
                // Agrega un usuario al mapa global de clientes conectados.
                clientesConectados.put(this.nombreUsuario, this); 
                System.out.println("[LOG] Usuario registrado como: " + this.nombreUsuario);

                // --- 2. ENVIAR MENÚ ---
                enviarMenuBienvenida();

                String mensajeRecibido;
                // --- 3. BUCLE DE ESCUCHA DEL CLIENTE ---
                // Se queda bloqueado en readLine() esperando que el cliente escriba algo.
                // Devuelve null únicamente si el cliente se desconectó.
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    
                    // Loguea en consola del servidor todo lo que pasa.
                    System.out.println("[LOG] [" + nombreUsuario + " DICE]: " + mensajeRecibido);

                    // Evalua qué comando ingresó el cliente
                    if (mensajeRecibido.equalsIgnoreCase("SALIR")) {
                        salida.println("Hasta luego! Cerrando conexión...");
                        break; // Rompe el while, lo que lleva al bloque finally y lo desconecta.
                    
                    } else if (mensajeRecibido.equalsIgnoreCase("LISTA")) {
                        // keySet() devuelve solo los nombres (las "llaves") del ConcurrentHashMap.
                        salida.println("Usuarios conectados: " + clientesConectados.keySet());
                    
                    } else if (mensajeRecibido.equalsIgnoreCase("HORA")) {
                        // --- NUEVO: FUNCIONALIDAD DE FECHA Y HORA ---
                        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String fechaHoraActual = LocalDateTime.now().format(formateador);
                        
                        salida.println("Fecha y hora del servidor: " + fechaHoraActual);
                        System.out.println("[LOG] [SERVIDOR RESPONDE A " + nombreUsuario + "]: Fecha y hora (" + fechaHoraActual + ")");
                    
                    } else if (mensajeRecibido.toUpperCase().startsWith("CRYPT ")) {
                        // LÓGICA DE ENCRIPTADOR: Corta la palabra "CRYPT " (son 6 caracteres) 
                        // y envia el resto del texto a la función de cifrado.
                        String textoACifrar = mensajeRecibido.substring(6).trim();
                        String respuesta = "Cifrado generado: " + aplicarCifradoInventado(textoACifrar);
                        salida.println(respuesta);
                        System.out.println("[LOG] [SERVIDOR RESPONDE A " + nombreUsuario + "]: " + respuesta);
                    
                    } else if (mensajeRecibido.startsWith("*")) {
                        // Si empieza con *, es un mensaje para otros usuarios. Se delega a otra función.
                        procesarMensajeEspecial(mensajeRecibido);
                    
                    } else {
                        // Manejo de errores básico si escriben cualquier cosa.
                        salida.println("Comando no reconocido. Usa *ALL, *Usuario, LISTA, HORA, CRYPT o SALIR.");
                    }
                }
            } catch (IOException e) {
                // Esto salta si hay un corte de internet o el cliente mata el proceso de su lado.
                System.err.println("[LOG] [ERROR] Comunicación interrumpida con " + nombreUsuario);
            } finally {
                // El bloque finally se ejecuta SIEMPRE. Ideal para limpiar datos.
                if (nombreUsuario != null) {
                    clientesConectados.remove(nombreUsuario); // Lo borra del mapa global
                    System.out.println("[LOG] [-] " + nombreUsuario + " se desconectó.");
                }
                // Intenta cerrar el socket físico.
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        // --- FUNCIONES AUXILIARES DE LA CLASE MANEJADOR ---

        // Función que cumple el REQUISITO de nombres únicos (Ej: Usuario, Usuario1, Usuario2)
        private String asignarNombreUnico(String nombreBase) {
            String nombreFinal = nombreBase;
            int contador = 1;
            // .containsKey() busca si "Usuario" ya está en el mapa. 
            // Si está, el bucle se ejecuta y prueba con "Usuario1". Si "Usuario1" está, prueba "Usuario2", etc.
            while (clientesConectados.containsKey(nombreFinal)) {
                nombreFinal = nombreBase + contador;
                contador++;
            }
            // Si el nombre final es distinto al que pidió el usuario, se le avisa del cambio.
            if (!nombreFinal.equals(nombreBase)) {
                salida.println("[AVISO] El nombre '" + nombreBase + "' ya está en uso. Se te asignó: " + nombreFinal);
            }
            return nombreFinal;
        }

        // Función del menu
        private void enviarMenuBienvenida() {
            salida.println("--------------------------------------------------");
            salida.println("Bienvenido! Tu nombre asignado es: " + nombreUsuario);
            salida.println("Comandos disponibles:");
            salida.println("  CRYPT <mensaje>          -> Aplica tu cifrado especial");
            salida.println("  *ALL <mensaje>           -> Enviar a todos los clientes");
            salida.println("  *User1,User2 <mensaje>   -> Enviar a clientes específicos");
            salida.println("  LISTA                    -> Ver quién está conectado");
            salida.println("  HORA                     -> Consulta la fecha y hora actual");
            salida.println("  SALIR                    -> Cierra la conexión");
            salida.println("--------------------------------------------------");
        }

        // Función que cumple el REQUISITO de enviar mensajes a 1, a varios o a todos.
        private void procesarMensajeEspecial(String input) {
            try {
                // Si recibe "*Juan,Pedro Hola", split("\\s+", 2) lo divide en el primer espacio que encuentre.
                // partes[0] = "*Juan,Pedro"
                // partes[1] = "Hola"
                String[] partes = input.split("\\s+", 2);
                
                // Le saca el primer caracter (el asterisco) para quedar solo con "Juan,Pedro"
                String cabecera = partes[0].substring(1); 
                
                // Si el usuario olvidó escribir un mensaje (ej: puso "*Juan " y enter), envia "(sin texto)"
                String mensaje = (partes.length > 1) ? partes[1] : "(sin texto)";

                if (cabecera.equalsIgnoreCase("ALL")) {
                    // BROADCAST: Recorre todo el mapa de clientes usando un forEach moderno.
                    clientesConectados.forEach((nombreDestino, manejadorDestino) -> {
                        // Verifica no enviarnos el mensaje a nosotros mismos.
                        if (!nombreDestino.equals(this.nombreUsuario)) {
                            // Usa el 'salida' (PrintWriter) del otro cliente para imprimir en SU pantalla.
                            manejadorDestino.salida.println("[TODOS] " + this.nombreUsuario + ": " + mensaje);
                        }
                    });
                } else {
                    // ENVÍO MÚLTIPLE: Divide la cabecera por comas. "Juan,Pedro" -> ["Juan", "Pedro"]
                    String[] destinatarios = cabecera.split(",");
                    for (String d : destinatarios) {
                        String destinoLimpio = d.trim(); // Quita espacios por si puso "Juan, Pedro"
                        
                        // Busca el hilo del destinatario en el mapa usando su nombre.
                        ManejadorCliente receptor = clientesConectados.get(destinoLimpio);
                        
                        // REQUISITO: Manejo de errores si el destinatario no existe.
                        if (receptor != null) {
                            receptor.salida.println("[PRIVADO de " + this.nombreUsuario + "]: " + mensaje);
                        } else {
                            // Le responde al remitente en que falló ese usuario específico.
                            salida.println("[ERROR] El usuario '" + destinoLimpio + "' no existe o no está conectado.");
                        }
                    }
                }
            } catch (Exception e) {
                salida.println("Error de formato. Ejemplo: *Juan,Maria Hola!");
            }
        }

        // Lógica de cifrado
        private String aplicarCifradoInventado(String texto) {
            // Invierte el string completo de atrás hacia adelante.
            String invertida = new StringBuilder(texto).reverse().toString();
            // Reemplaza las vocales por símbolos específicos.
            String resultado = invertida
                    .replace("a", "@").replace("A", "@")
                    .replace("e", "3").replace("E", "3")
                    .replace("i", "!").replace("I", "!")
                    .replace("o", "0").replace("O", "0")
                    .replace("u", "v").replace("U", "v");
            // Agrega el sufijo de seguridad al final.
            return resultado + "#SEC";
        }
    }  
}
