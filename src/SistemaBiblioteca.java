import java.util.Scanner;

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
        String cod = validarCodigoDisponible(codigo);
        validarTexto(titulo, "El titulo del libro");
        validarTexto(autor, "El autor");
        validarTexto(usuario, "El nombre del usuario");
        if (dias < DIAS_MINIMOS || dias > DIAS_MAXIMOS) {
            throw new Exception("Los dias de prestamo deben estar entre " + DIAS_MINIMOS
                    + " y " + DIAS_MAXIMOS);
        }
        LibroPrestamo prestamo = new LibroPrestamo(cod, titulo.trim(), autor.trim(), usuario.trim(), dias);
        listaGeneral.agregar(prestamo);
        colaPendientes.encolar(prestamo);
        return prestamo;
    }

    /**
     * Verifica que un codigo sea valido y que no este registrado. El menu lo usa justo despues
     * de leer el codigo, para no pedir los demas datos si el codigo ya existe.
     */
    public String validarCodigoDisponible(String codigo) throws Exception {
        String cod = validarCodigo(codigo);
        if (listaGeneral.contiene(LibroPrestamo.conCodigo(cod))) {
            throw new Exception("Ya existe un prestamo con el codigo " + cod);
        }
        return cod;
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

    // ==================================================================
    // Menu de consola
    // ==================================================================

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            System.out.println();
            try {
                switch (opcion) {
                    case 1:
                        opcionRegistrar(sistema);
                        break;
                    case 2:
                        opcionVerRegistrados(sistema);
                        break;
                    case 3:
                        System.out.println("PRESTAMOS PENDIENTES (orden de entrega):");
                        System.out.println(sistema.getColaPendientes().mostrar());
                        break;
                    case 4:
                        LibroPrestamo procesado = sistema.procesarSiguiente();
                        System.out.println("Libro entregado: " + procesado);
                        break;
                    case 5:
                        System.out.println("HISTORIAL DE ENTREGAS (la cima es la mas reciente):");
                        System.out.println(sistema.getHistorialProcesados().mostrar());
                        break;
                    case 6:
                        LibroPrestamo encontrado = sistema.buscarPorCodigo(leerTexto("Codigo a buscar: "));
                        System.out.println("Encontrado: " + encontrado);
                        break;
                    case 7:
                        LibroPrestamo cancelado = sistema.cancelarPendiente(leerTexto("Codigo a cancelar: "));
                        System.out.println("Prestamo cancelado: " + cancelado);
                        break;
                    case 8:
                        LibroPrestamo devuelto = sistema.deshacerUltimoProcesamiento();
                        System.out.println("Se deshizo la entrega. Vuelve a pendientes: " + devuelto);
                        break;
                    case 9:
                        opcionCantidades(sistema);
                        break;
                    case 10:
                        System.out.println("Fin del programa.");
                        break;
                    default:
                        System.out.println("Opcion no valida. Elija un numero del 1 al 10.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println();
        } while (opcion != 10);
    }

    private static void mostrarMenu() {
        System.out.println("========= BIBLIOTECA - GESTION DE PRESTAMOS =========");
        System.out.println(" 1. Registrar prestamo");
        System.out.println(" 2. Ver todos los prestamos registrados");
        System.out.println(" 3. Ver prestamos pendientes");
        System.out.println(" 4. Procesar siguiente prestamo (entregar libro)");
        System.out.println(" 5. Ver historial de prestamos entregados");
        System.out.println(" 6. Buscar prestamo por codigo");
        System.out.println(" 7. Cancelar prestamo pendiente");
        System.out.println(" 8. Deshacer ultima entrega");
        System.out.println(" 9. Ver cantidad de prestamos");
        System.out.println("10. Salir");
    }

    private static void opcionRegistrar(SistemaBiblioteca sistema) throws Exception {
        String codigo = sistema.validarCodigoDisponible(leerTexto("Codigo del prestamo (ej: PR-001): "));
        String titulo = leerTexto("Titulo del libro: ");
        String autor = leerTexto("Autor: ");
        String usuario = leerTexto("Nombre del usuario: ");
        int dias = leerEntero("Dias de prestamo (" + DIAS_MINIMOS + "-" + DIAS_MAXIMOS + "): ");
        LibroPrestamo nuevo = sistema.registrar(codigo, titulo, autor, usuario, dias);
        System.out.println("Prestamo registrado y enviado a pendientes: " + nuevo);
    }

    private static void opcionVerRegistrados(SistemaBiblioteca sistema) {
        int orden = leerEntero("Orden: 1 = del primero al ultimo, 2 = del ultimo al primero: ");
        System.out.println("TODOS LOS PRESTAMOS REGISTRADOS:");
        if (orden == 2) {
            System.out.println(sistema.getListaGeneral().mostrarAtras());
        } else {
            System.out.println(sistema.getListaGeneral().mostrarAdelante());
        }
    }

    private static void opcionCantidades(SistemaBiblioteca sistema) {
        int[] c = sistema.cantidades();
        System.out.println("Registrados (lista general): " + c[0]);
        System.out.println("Pendientes  (cola)         : " + c[1]);
        System.out.println("Entregados  (pila)         : " + c[2]);
        System.out.println("Cancelados                 : " + c[3]);
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return entrada.hasNextLine() ? entrada.nextLine() : "";
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String linea = leerTexto(mensaje).trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                if (!entrada.hasNextLine() && linea.isEmpty()) {
                    return 10;   // fin de la entrada: salir
                }
                System.out.println("Debe escribir un numero entero.");
            }
        }
    }
}
