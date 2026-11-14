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

    /** 6. Buscar en la lista general por codigo (usa equals de LibroPrestamo). */
    public LibroPrestamo buscarPorCodigo(String codigo) throws Exception {
        String cod = validarCodigo(codigo);
        if (listaGeneral.esVacia()) {
            throw new Exception("No hay prestamos registrados");
        }
        LibroPrestamo buscado = LibroPrestamo.conCodigo(cod);
        if (!listaGeneral.contiene(buscado)) {
            throw new Exception("No existe un prestamo con el codigo " + cod);
        }
        int posicion = listaGeneral.buscarDato(buscado);
        return (LibroPrestamo) listaGeneral.buscarDato(posicion);
    }

    /**
     * 7. Cancelar un pendiente usando una cola auxiliar.
     * Se desencolan todos: el que coincide se marca CANCELADO y no se vuelve a encolar;
     * los demas pasan a la auxiliar y luego regresan a la cola en el mismo orden.
     */
    public LibroPrestamo cancelarPendiente(String codigo) throws Exception {
        LibroPrestamo prestamo = buscarPorCodigo(codigo);   // valida que exista
        if (!LibroPrestamo.PENDIENTE.equals(prestamo.getEstado())) {
            throw new Exception("El prestamo " + prestamo.getCodigoPrestamo() + " no esta pendiente (estado: "
                    + prestamo.getEstado() + "). Solo se cancelan prestamos pendientes");
        }

        Cola auxiliar = new Cola();
        LibroPrestamo cancelado = null;
        while (!colaPendientes.esVacia()) {
            LibroPrestamo actual = (LibroPrestamo) colaPendientes.desencolar();
            if (cancelado == null && actual.equals(prestamo)) {
                cancelado = actual;               // no se pasa a la auxiliar
            } else {
                auxiliar.encolar(actual);
            }
        }
        while (!auxiliar.esVacia()) {
            colaPendientes.encolar(auxiliar.desencolar());
        }

        if (cancelado == null) {
            throw new Exception("El prestamo " + prestamo.getCodigoPrestamo() + " no se encontro en la cola");
        }
        cancelado.setEstado(LibroPrestamo.CANCELADO);
        return cancelado;
    }

    /** 8. Deshacer: la ultima entrega sale de la pila y vuelve a la cola como pendiente. */
    public LibroPrestamo deshacerUltimoProcesamiento() throws Exception {
        if (historialProcesados.esVacia()) {
            throw new Exception("No hay entregas para deshacer");
        }
        LibroPrestamo ultimo = (LibroPrestamo) historialProcesados.desapilar();
        ultimo.setEstado(LibroPrestamo.PENDIENTE);
        colaPendientes.encolar(ultimo);
        return ultimo;
    }

    /** 9. Cantidades: [registrados, pendientes, entregados, cancelados]. */
    public int[] cantidades() {
        int cancelados = 0;
        for (Object o : listaGeneral.aArreglo()) {
            if (LibroPrestamo.CANCELADO.equals(((LibroPrestamo) o).getEstado())) {
                cancelados++;
            }
        }
        return new int[] {
            listaGeneral.cuentaElementos(),
            colaPendientes.tamanio(),
            historialProcesados.tamanio(),
            cancelados
        };
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
