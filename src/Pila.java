/**
 * Pila (LIFO: el ultimo en entrar es el primero en salir) con nodos enlazados.
 *
 * cima -> izquierda -> ... -> base
 * Se apila y se desapila siempre por la cima.
 *
 * En el sistema es el historial de prestamos entregados y permite deshacer
 * la ultima entrega.
 * Basada en el ejemplo del docente John Carlos Arrieta Arrieta.
 */
public class Pila {

    private int cuenta;
    private Nodo cima;   // el tope de la pila

    public Pila() {
        cuenta = 0;
        cima = null;
    }

    public boolean esVacia() {
        return cuenta == 0;
    }

    /** Apilar (push): el nuevo nodo queda encima de la cima actual. */
    public void apilar(Object dato) throws Exception {
        if (dato == null) {
            throw new Exception("El dato no puede ser nulo");
        }
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            cima = nuevo;
        } else {
            nuevo.setIzquierda(cima);   // el nuevo apunta hacia abajo a la cima anterior
            cima.setDerecha(nuevo);     // la cima anterior apunta hacia arriba al nuevo
            cima = nuevo;
        }
        ++cuenta;
    }

    /** Desapilar (pop): saca el elemento de la cima. */
    public Object desapilar() throws Exception {
        if (esVacia()) {
            throw new Exception("La pila esta vacia");
        }
        Object dato = cima.getDato();
        cima = cima.getIzquierda();     // el nodo de abajo pasa a ser la cima
        if (cima != null) {
            cima.setDerecha(null);
        }
        --cuenta;
        return dato;
    }

    /** Consulta la cima sin sacarla. */
    public Object peek() throws Exception {
        if (esVacia()) {
            throw new Exception("La pila esta vacia");
        }
        return cima.getDato();
    }

    public int tamanio() {
        return cuenta;
    }
}
