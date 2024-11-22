package servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorTCP {
	private String[] respuesta;
	private int[] combinacionGanadora;
	private int reintegro;
	private int complementario;
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;

	/**
	 * Constructor
	 */
	public ServidorTCP(int puerto) {
		try {
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexión del cliente...");
			socketCliente = socketServidor.accept();
			System.out.println("Cliente conectado.");
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("Error al iniciar el servidor.");
			System.exit(-1);
		}

		// Configurar respuestas
		this.respuesta = new String[] { "Boleto inválido - Números repetidos",
				"Boleto inválido - números incorrectos (1-49)", "6 aciertos", "5 aciertos + complementario",
				"5 aciertos", "4 aciertos", "3 aciertos", "Reintegro", "Sin premio" };

		// Generar combinación ganadora
		generarCombinacion();
		imprimirCombinacion();
	}

	/**
	 * Leer combinación del cliente.
	 */
	public String leerCombinacion() {
		try {
			return entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Evaluar el boleto recibido.
	 */
	public String comprobarBoleto(String boletoStr) {
		try {
			// Parsear el boleto
			String[] numerosStr = boletoStr.replaceAll("[\\[\\]]", "").split(", ");
			int[] boleto = Arrays.stream(numerosStr).mapToInt(Integer::parseInt).toArray();

			// Validar número de elementos y rango
			if (new HashSet<>(Arrays.asList(boleto)).size() != 6)
				return respuesta[0]; // Repetidos
			for (int num : boleto) {
				if (num < 1 || num > 49)
					return respuesta[1]; // Fuera de rango
			}

			// Contar aciertos
			Set<Integer> numerosGanadores = new HashSet<>();
			for (int num : combinacionGanadora)
				numerosGanadores.add(num);

			int aciertos = 0;
			for (int num : boleto) {
				if (numerosGanadores.contains(num))
					aciertos++;
			}

			// Determinar premio
			switch (aciertos) {
			case 6:
				return respuesta[2];
			case 5:
				return numerosGanadores.contains(complementario) ? respuesta[3] : respuesta[4];
			case 4:
				return respuesta[5];
			case 3:
				return respuesta[6];
			default:
				if (Arrays.asList(boleto).contains(reintegro))
					return respuesta[7];
				return respuesta[8];
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error al evaluar boleto.";
		}
	}

	/**
	 * Enviar respuesta al cliente.
	 */
	public void enviarRespuesta(String respuesta) {
		salida.println(respuesta);
	}

	/**
	 * Finalizar el servidor.
	 */
	public void finSesion() {
		try {
			salida.close();
			entrada.close();
			socketCliente.close();
			socketServidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Servidor terminado.");
	}

	/**
	 * Generar combinación ganadora.
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new HashSet<>();
		Random random = new Random();
		while (numeros.size() < 6) {
			numeros.add(random.nextInt(49) + 1);
		}
		combinacionGanadora = numeros.stream().mapToInt(Integer::intValue).toArray();
		reintegro = random.nextInt(49) + 1;
		complementario = random.nextInt(49) + 1;
	}

	/**
	 * Imprimir combinación ganadora en el servidor.
	 */
	private void imprimirCombinacion() {
		System.out.println("Combinación ganadora: " + Arrays.toString(combinacionGanadora));
		System.out.println("Complementario: " + complementario);
		System.out.println("Reintegro: " + reintegro);
	}
}
