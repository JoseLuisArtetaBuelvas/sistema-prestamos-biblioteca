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
