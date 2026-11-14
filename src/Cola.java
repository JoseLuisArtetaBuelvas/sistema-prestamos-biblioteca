/**
 * Cola (FIFO: el primero en llegar es el primero en salir) con nodos enlazados.
 *
 * frente -> ... -> fin
 * Se encola por el fin y se desencola por el frente.
 *
 * En el sistema guarda los prestamos pendientes por entregar, en orden de llegada.
 * Basada en el ejemplo del docente John Carlos Arrieta Arrieta.
 */
public class Cola {

    private int cuenta;
    private Nodo frente;   // primer elemento (el proximo en salir)
    private Nodo fin;      // ultimo elemento (el ultimo en llegar)

    public Cola() {
        cuenta = 0;
        frente = null;
        fin = null;
    }

    public boolean esVacia() {
        return cuenta == 0;
    }

    /** Encolar: agrega al final. */
    public void encolar(Object dato) throws Exception {
        if (dato == null) {
            throw new Exception("El dato no puede ser nulo");
        }
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setDerecha(nuevo);
            nuevo.setIzquierda(fin);
            fin = nuevo;
        }
        ++cuenta;
    }

    /** Desencolar: saca el elemento del frente. */
    public Object desencolar() throws Exception {
        if (esVacia()) {
            throw new Exception("La cola esta vacia");
        }
        Object dato = frente.getDato();
        frente = frente.getDerecha();
        if (frente != null) {
            frente.setIzquierda(null);
        } else {
            fin = null;   // la cola quedo vacia
        }
        --cuenta;
        return dato;
    }

    /** Consulta el frente sin sacarlo. */
    public Object peek() throws Exception {
        if (esVacia()) {
            throw new Exception("La cola esta vacia");
        }
        return frente.getDato();
    }

    public int tamanio() {
        return cuenta;
    }
}
