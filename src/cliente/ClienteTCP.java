package cliente;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClienteTCP {
	private Socket socketCliente = null;
	private BufferedReader entrada = null;
	private PrintWriter salida = null;

	/**
	 * Constructor
	 */
	public ClienteTCP(String ip, int puerto) {
		try {
			socketCliente = new Socket(ip, puerto);
			System.out.println("Conexión establecida con el servidor.");
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.printf("Imposible conectar con ip: %s / puerto: %d\n", ip, puerto);
			System.exit(-1);
		}
	}

	/**
	 * Envía una combinación de números al servidor y recibe la respuesta.
	 */
	public String comprobarBoleto(int[] combinacion) {
		try {
			// Enviar la combinación como una cadena
			salida.println(Arrays.toString(combinacion));
			// Recibir la respuesta
			return entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error al comprobar boleto.";
		}
	}

	/**
	 * Finalizar conexión con el servidor.
	 */
	public void finSesion() {
		try {
			salida.println("FIN");
			salida.close();
			entrada.close();
			socketCliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Cliente terminado.");
	}
}
