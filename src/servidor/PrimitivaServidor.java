package servidor;

public class PrimitivaServidor {
	public static void main(String[] args) {
		ServidorTCP canal = new ServidorTCP(5555);
		String linea;
		do {
			linea = canal.leerCombinacion();
			String respuesta = canal.comprobarBoleto(linea);
			canal.enviarRespuesta(respuesta);
		} while (!linea.equals("FIN"));
		canal.finSesion();
	}
}
