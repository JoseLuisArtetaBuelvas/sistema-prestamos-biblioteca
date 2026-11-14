/**
 * Nodo doblemente enlazado: guarda un dato y dos referencias.
 *
 * izquierda -> nodo anterior
 * derecha   -> nodo siguiente
 *
 * Es la pieza basica que usan Lista, Cola y Pila.
 * Basado en el ejemplo del docente John Carlos Arrieta Arrieta.
 */
public class Nodo {

    private Object dato;
    private Nodo izquierda;
    private Nodo derecha;

    public Nodo() {
    }

    public Nodo(Object dato) {
        this.dato = dato;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public Nodo getIzquierda() {
        return izquierda;
    }

    public void setIzquierda(Nodo vecino) {
        this.izquierda = vecino;
    }

    public Nodo getDerecha() {
        return derecha;
    }

    public void setDerecha(Nodo vecino) {
        this.derecha = vecino;
    }
}
