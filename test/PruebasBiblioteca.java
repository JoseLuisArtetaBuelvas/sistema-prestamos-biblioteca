/**
 * Casos de prueba de las estructuras y del sistema de prestamos (sin librerias externas).
 *
 * Ejecutar:
 *   java -cp out PruebasBiblioteca
 */
public class PruebasBiblioteca {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        probarLista();
        probarCola();
        probarPila();
        probarLibroPrestamo();
        probarSistema();
        probarValidaciones();

        System.out.println();
        System.out.println("Resultado: " + aprobadas + " aprobadas, " + fallidas + " fallidas.");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    private static void probarLista() throws Exception {
        System.out.println("== Lista ==");
        Lista l = new Lista();
        verificar("lista nueva vacia", l.esVacia(), true);
        l.agregar("B");
        l.agregar("C");
        l.agregarAlInicio("A");
        l.agregarEnPosicion(3, "E");
        l.agregarEnPosicion(3, "D");
        verificar("agregar, agregarAlInicio y agregarEnPosicion", l.mostrarAdelante(),
                "0. A\n1. B\n2. C\n3. D\n4. E");
        verificar("mostrarAtras usa las referencias izquierda", l.mostrarAtras(),
                "4. E\n3. D\n2. C\n1. B\n0. A");
        verificar("cuentaElementos", l.cuentaElementos(), 5);
        verificar("buscarDato(indice)", l.buscarDato(2), "C");
        verificar("buscarDato(dato)", l.buscarDato("D"), 3);
        verificar("contiene existente", l.contiene("E"), true);
        verificar("contiene inexistente", l.contiene("Z"), false);
        verificarExcepcion("buscarDato(dato) inexistente", () -> l.buscarDato("Z"));
        verificarExcepcion("buscarDato(indice) fuera de rango", () -> l.buscarDato(9));
        verificarExcepcion("agregarEnPosicion fuera de rango", () -> l.agregarEnPosicion(7, "X"));
        verificar("eliminarEnPosicion (intermedio)", l.eliminarEnPosicion(2), "C");
        verificar("eliminarPrimero", l.eliminarPrimero(), "A");
        verificar("eliminarUltimo", l.eliminarUltimo(), "E");
        verificar("enlaces correctos tras eliminar (adelante)", l.mostrarAdelante(), "0. B\n1. D");
        verificar("enlaces correctos tras eliminar (atras)", l.mostrarAtras(), "1. D\n0. B");
        l.eliminarUltimo();
        l.eliminarUltimo();
        verificar("lista vacia tras eliminar todo", l.esVacia(), true);
        verificarExcepcion("eliminarPrimero en lista vacia", l::eliminarPrimero);
        l.agregar("X");
        l.limpiar();
        verificar("limpiar", l.cuentaElementos(), 0);
        verificarExcepcion("agregar null", () -> l.agregar(null));
    }

    private static void probarCola() throws Exception {
        System.out.println("== Cola (FIFO) ==");
        Cola c = new Cola();
        c.encolar("1");
        c.encolar("2");
        c.encolar("3");
        verificar("peek muestra el frente", c.peek(), "1");
        verificar("desencolar saca el primero que llego", c.desencolar(), "1");
        verificar("tamanio", c.tamanio(), 2);
        verificar("contiene", c.contiene("3"), true);
        verificar("mostrar del frente al fin", c.mostrar(), "1. 2\n2. 3");
        c.desencolar();
        c.desencolar();
        verificar("cola vacia", c.esVacia(), true);
        verificarExcepcion("desencolar en cola vacia", c::desencolar);
        c.encolar("4");
        verificar("encolar despues de vaciar", c.peek(), "4");
        c.limpiar();
        verificar("limpiar", c.tamanio(), 0);
    }

    private static void probarPila() throws Exception {
        System.out.println("== Pila (LIFO) ==");
        Pila p = new Pila();
        p.apilar("A");
        p.apilar("B");
        p.apilar("C");
        verificar("peek muestra la cima", p.peek(), "C");
        verificar("buscar en la cima = 1", p.buscar("C"), 1);
        verificar("buscar en la base = 3", p.buscar("A"), 3);
        verificar("buscar inexistente = -1", p.buscar("Z"), -1);
        verificar("desapilar saca el ultimo que entro", p.desapilar(), "C");
        verificar("nueva cima", p.peek(), "B");
        verificar("contiene", p.contiene("A"), true);
        p.desapilar();
        p.desapilar();
        verificarExcepcion("desapilar en pila vacia", p::desapilar);
        p.apilar("D");
        p.limpiar();
        verificar("limpiar", p.esVacia(), true);
    }

    private static void probarLibroPrestamo() {
        System.out.println("== LibroPrestamo ==");
        LibroPrestamo a = new LibroPrestamo("pr-001", "Titulo", "Autor", "Usuario", 7);
        LibroPrestamo b = LibroPrestamo.conCodigo("PR-001");
        verificar("codigo se guarda en mayusculas", a.getCodigoPrestamo(), "PR-001");
        verificar("equals compara solo el codigo", a.equals(b), true);
        verificar("hashCode consistente con equals", a.hashCode() == b.hashCode(), true);
        verificar("estado inicial", a.getEstado(), LibroPrestamo.PENDIENTE);
    }

    private static void probarSistema() throws Exception {
        System.out.println("== Sistema de prestamos ==");
        SistemaBiblioteca s = new SistemaBiblioteca();
        s.registrar("PR-001", "Cien anos de soledad", "Gabriel Garcia Marquez", "Ana Perez", 7);
        s.registrar("PR-002", "El principito", "Antoine de Saint-Exupery", "Luis Gomez", 5);
        s.registrar("PR-003", "La voragine", "Jose Eustasio Rivera", "Maria Ruiz", 10);
        s.registrar("PR-004", "Maria", "Jorge Isaacs", "Carlos Diaz", 3);
        verificar("registrar agrega a la lista", s.getListaGeneral().cuentaElementos(), 4);
        verificar("registrar encola", s.getColaPendientes().tamanio(), 4);

        LibroPrestamo p1 = s.procesarSiguiente();
        verificar("procesa en orden de llegada", p1.getCodigoPrestamo(), "PR-001");
        verificar("procesado queda ENTREGADO", p1.getEstado(), LibroPrestamo.ENTREGADO);
        s.procesarSiguiente();
        verificar("historial con 2 entregas", s.getHistorialProcesados().tamanio(), 2);
        verificar("cima del historial es la ultima entrega",
                ((LibroPrestamo) s.getHistorialProcesados().peek()).getCodigoPrestamo(), "PR-002");

        LibroPrestamo c = s.cancelarPendiente("pr-004");
        verificar("cancelar marca CANCELADO", c.getEstado(), LibroPrestamo.CANCELADO);
        verificar("cancelado sale de la cola", s.getColaPendientes().tamanio(), 1);
        verificar("cancelado sigue en la lista general", s.getListaGeneral().cuentaElementos(), 4);
        verificar("la cola conserva el orden", s.getColaPendientes().mostrar().startsWith("1. [PR-003]"), true);
        verificarExcepcion("no se cancela un entregado", () -> s.cancelarPendiente("PR-001"));
        verificarExcepcion("no se cancela dos veces", () -> s.cancelarPendiente("PR-004"));

        LibroPrestamo d = s.deshacerUltimoProcesamiento();
        verificar("deshacer saca la cima (PR-002)", d.getCodigoPrestamo(), "PR-002");
        verificar("deshacer lo deja PENDIENTE", d.getEstado(), LibroPrestamo.PENDIENTE);
        verificar("deshacer lo encola al final", s.getColaPendientes().mostrar(),
                "1. " + s.buscarPorCodigo("PR-003") + "\n2. " + d);

        verificar("buscar por codigo", s.buscarPorCodigo("pr-003").getTituloLibro(), "La voragine");
        verificarExcepcion("buscar codigo inexistente", () -> s.buscarPorCodigo("PR-999"));

        int[] cant = s.cantidades();
        verificar("cantidades [registrados, pendientes, entregados, cancelados]",
                cant[0] + "," + cant[1] + "," + cant[2] + "," + cant[3], "4,2,1,1");

        s.procesarSiguiente();
        s.procesarSiguiente();
        verificarExcepcion("procesar sin pendientes", s::procesarSiguiente);
        s.deshacerUltimoProcesamiento();
        s.deshacerUltimoProcesamiento();
        s.deshacerUltimoProcesamiento();
        verificarExcepcion("deshacer sin historial", s::deshacerUltimoProcesamiento);
    }

    private static void probarValidaciones() throws Exception {
        System.out.println("== Validaciones ==");
        SistemaBiblioteca s = new SistemaBiblioteca();
        s.registrar("PR-001", "Titulo", "Autor", "Usuario", 7);
        verificarExcepcion("codigo duplicado (sin importar mayusculas)",
                () -> s.registrar("pr-001", "Otro", "Autor", "Usuario", 7));
        verificarExcepcion("codigo vacio", () -> s.registrar("  ", "T", "A", "U", 7));
        verificarExcepcion("codigo con simbolos", () -> s.registrar("PR#1", "T", "A", "U", 7));
        verificarExcepcion("titulo vacio", () -> s.registrar("PR-002", "", "A", "U", 7));
        verificarExcepcion("dias en 0", () -> s.registrar("PR-002", "T", "A", "U", 0));
        verificarExcepcion("dias mayores a 30", () -> s.registrar("PR-002", "T", "A", "U", 31));
        verificar("ningun registro invalido entro a la lista", s.getListaGeneral().cuentaElementos(), 1);
        verificar("ningun registro invalido entro a la cola", s.getColaPendientes().tamanio(), 1);
    }

    // ------------------------------------------------------------------

    private interface Accion {
        void ejecutar() throws Exception;
    }

    private static void verificar(String nombre, Object obtenido, Object esperado) {
        if (obtenido.equals(esperado)) {
            aprobadas++;
            System.out.println("  [OK]    " + nombre);
        } else {
            fallidas++;
            System.out.println("  [FALLA] " + nombre + " -> esperado <" + esperado + ">, obtenido <" + obtenido + ">");
        }
    }

    private static void verificarExcepcion(String nombre, Accion accion) {
        try {
            accion.ejecutar();
            fallidas++;
            System.out.println("  [FALLA] " + nombre + " -> se esperaba una excepcion");
        } catch (Exception e) {
            aprobadas++;
            System.out.println("  [OK]    " + nombre + "  (" + e.getMessage() + ")");
        }
    }
}
