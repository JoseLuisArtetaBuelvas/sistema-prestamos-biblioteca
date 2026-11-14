/**
 * Sistema de gestion de prestamos de una biblioteca (caso 3).
 *
 * Estructuras:
 *   listaGeneral        (Lista) -> registro de TODOS los prestamos, en cualquier estado
 *   colaPendientes      (Cola)  -> prestamos esperando ser entregados, en orden de llegada (FIFO)
 *   historialProcesados (Pila)  -> prestamos entregados; la cima es la ultima entrega (LIFO)
 *
 * Un mismo objeto LibroPrestamo esta en la lista y, segun su estado, tambien en la cola o
 * en la pila: las estructuras guardan referencias al mismo objeto, por eso al cambiar el
 * estado se ve el cambio en todas.
 */
public class SistemaBiblioteca {

    public static final int DIAS_MINIMOS = 1;
    public static final int DIAS_MAXIMOS = 30;

    private final Lista listaGeneral = new Lista();
    private final Cola colaPendientes = new Cola();
    private final Pila historialProcesados = new Pila();

    // ==================================================================
    // Logica del sistema
    // ==================================================================

    /** 1. Registrar: valida, guarda en la lista general y encola como pendiente. */
    public LibroPrestamo registrar(String codigo, String titulo, String autor,
                                   String usuario, int dias) throws Exception {
        String cod = validarCodigo(codigo);
        validarTexto(titulo, "El titulo del libro");
        validarTexto(autor, "El autor");
        validarTexto(usuario, "El nombre del usuario");
        if (dias < DIAS_MINIMOS || dias > DIAS_MAXIMOS) {
            throw new Exception("Los dias de prestamo deben estar entre " + DIAS_MINIMOS
                    + " y " + DIAS_MAXIMOS);
        }
        if (listaGeneral.contiene(LibroPrestamo.conCodigo(cod))) {
            throw new Exception("Ya existe un prestamo con el codigo " + cod);
        }

        LibroPrestamo prestamo = new LibroPrestamo(cod, titulo.trim(), autor.trim(), usuario.trim(), dias);
        listaGeneral.agregar(prestamo);
        colaPendientes.encolar(prestamo);
        return prestamo;
    }

    /** 4. Procesar: el primero de la cola se entrega y pasa a la pila de historial. */
    public LibroPrestamo procesarSiguiente() throws Exception {
        if (colaPendientes.esVacia()) {
            throw new Exception("No hay prestamos pendientes por entregar");
        }
        LibroPrestamo procesado = (LibroPrestamo) colaPendientes.desencolar();
        procesado.setEstado(LibroPrestamo.ENTREGADO);
        historialProcesados.apilar(procesado);
        return procesado;
    }

    public Lista getListaGeneral() {
        return listaGeneral;
    }

    public Cola getColaPendientes() {
        return colaPendientes;
    }

    public Pila getHistorialProcesados() {
        return historialProcesados;
    }

    // ------------------------------------------------------------------
    // Validaciones
    // ------------------------------------------------------------------

    /** El codigo es obligatorio, en mayusculas, con letras, numeros y guiones (ej: PR-001). */
    private String validarCodigo(String codigo) throws Exception {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("El codigo de prestamo es obligatorio");
        }
        String cod = codigo.trim().toUpperCase();
        if (!cod.matches("[A-Z0-9-]{3,15}")) {
            throw new Exception("Codigo invalido: use de 3 a 15 letras, numeros o guiones (ej: PR-001)");
        }
        return cod;
    }

    private void validarTexto(String valor, String campo) throws Exception {
        if (valor == null || valor.trim().isEmpty()) {
            throw new Exception(campo + " es obligatorio");
        }
    }
}
