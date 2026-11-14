/**
 * Lista doblemente enlazada.
 *
 * primero -> ... -> ultimo  (recorrido hacia adelante con getDerecha)
 * ultimo  -> ... -> primero (recorrido hacia atras con getIzquierda)
 *
 * En el sistema se usa como registro general de todos los prestamos.
 * Basada en el ejemplo del docente John Carlos Arrieta Arrieta.
 */
public class Lista {

    private int cuenta;
    private Nodo primero;
    private Nodo ultimo;

    public Lista() {
        cuenta = 0;
        primero = null;
        ultimo = null;
    }

    public boolean esVacia() {
        return cuenta == 0;
    }

    // ------------------------------------------------------------------
    // Agregar
    // ------------------------------------------------------------------

    /** Agrega al final de la lista. */
    public void agregar(Object dato) throws Exception {
        validarDato(dato);
        Nodo objetivo = new Nodo(dato);
        if (esVacia()) {
            primero = objetivo;
            ultimo = objetivo;
        } else {
            ultimo.setDerecha(objetivo);     // el ultimo actual apunta al nuevo
            objetivo.setIzquierda(ultimo);   // el nuevo apunta hacia atras al ultimo actual
            ultimo = objetivo;               // el nuevo pasa a ser el ultimo
        }
        ++cuenta;
    }

    /** Agrega al inicio de la lista. */
    public void agregarAlInicio(Object dato) throws Exception {
        validarDato(dato);
        Nodo objetivo = new Nodo(dato);
        if (esVacia()) {
            primero = objetivo;
            ultimo = objetivo;
        } else {
            objetivo.setDerecha(primero);
            primero.setIzquierda(objetivo);
            primero = objetivo;
        }
        ++cuenta;
    }

    /**
     * Agrega en la posicion indicada (0 = inicio, cuenta = final).
     * Los nodos desde esa posicion se corren una posicion a la derecha.
     */
    public void agregarEnPosicion(int indice, Object dato) throws Exception {
        validarDato(dato);
        if (indice < 0 || indice > cuenta) {
            throw new Exception("La posicion " + indice + " esta fuera del rango 0 - " + cuenta);
        }
        if (indice == 0) {
            agregarAlInicio(dato);
            return;
        }
        if (indice == cuenta) {
            agregar(dato);
            return;
        }
        Nodo actual = buscar(indice);           // nodo que hoy ocupa la posicion
        Nodo anterior = actual.getIzquierda();
        Nodo objetivo = new Nodo(dato);

        objetivo.setIzquierda(anterior);
        objetivo.setDerecha(actual);
        anterior.setDerecha(objetivo);
        actual.setIzquierda(objetivo);
        ++cuenta;
    }

    // ------------------------------------------------------------------
    // Eliminar
    // ------------------------------------------------------------------

    public Object eliminarPrimero() throws Exception {
        if (esVacia()) {
            throw new Exception("La lista esta vacia");
        }
        Object dato = primero.getDato();
        primero = primero.getDerecha();
        if (primero == null) {
            ultimo = null;                      // la lista quedo vacia
        } else {
            primero.setIzquierda(null);         // se suelta el nodo eliminado
        }
        --cuenta;
        return dato;
    }

    public Object eliminarUltimo() throws Exception {
        if (esVacia()) {
            throw new Exception("La lista esta vacia");
        }
        Object dato = ultimo.getDato();
        ultimo = ultimo.getIzquierda();
        if (ultimo == null) {
            primero = null;
        } else {
            ultimo.setDerecha(null);
        }
        --cuenta;
        return dato;
    }

    public Object eliminarEnPosicion(int indice) throws Exception {
        Nodo objetivo = buscar(indice);         // valida lista vacia y rango
        if (objetivo == primero) {
            return eliminarPrimero();
        }
        if (objetivo == ultimo) {
            return eliminarUltimo();
        }
        // Nodo intermedio: sus vecinos se apuntan entre si
        Nodo anterior = objetivo.getIzquierda();
        Nodo siguiente = objetivo.getDerecha();
        anterior.setDerecha(siguiente);
        siguiente.setIzquierda(anterior);
        --cuenta;
        return objetivo.getDato();
    }

    // ------------------------------------------------------------------
    // Buscar
    // ------------------------------------------------------------------

    /** Recorre desde el primero hasta la posicion pedida. */
    private Nodo buscar(int indice) throws Exception {
        if (esVacia()) {
            throw new Exception("La lista esta vacia");
        }
        if (indice < 0 || indice >= cuenta) {
            throw new Exception("La posicion " + indice + " esta fuera del rango 0 - " + (cuenta - 1));
        }
        Nodo actual = primero;
        for (int i = 0; i < indice; i++) {
            actual = actual.getDerecha();
        }
        return actual;
    }

    /** Posicion del dato (usa equals) o -1 si no esta. */
    private int indiceDe(Object dato) {
        Nodo actual = primero;
        for (int i = 0; i < cuenta; i++) {
            if (actual.getDato().equals(dato)) {
                return i;
            }
            actual = actual.getDerecha();
        }
        return -1;
    }

    public Object buscarDato(int indice) throws Exception {
        return buscar(indice).getDato();
    }

    public int buscarDato(Object dato) throws Exception {
        if (esVacia()) {
            throw new Exception("La lista esta vacia");
        }
        int indice = indiceDe(dato);
        if (indice == -1) {
            throw new Exception("El elemento no esta en la lista");
        }
        return indice;
    }

    public boolean contiene(Object dato) {
        return dato != null && indiceDe(dato) != -1;
    }

    // ------------------------------------------------------------------
    // Otros
    // ------------------------------------------------------------------

    public int cuentaElementos() {
        return cuenta;
    }

    /** Vacia la lista soltando las referencias de inicio y fin. */
    public void limpiar() {
        primero = null;
        ultimo = null;
        cuenta = 0;
    }

    /** Copia los datos a un arreglo en un solo recorrido (util para reportes). */
    public Object[] aArreglo() {
        Object[] datos = new Object[cuenta];
        Nodo actual = primero;
        for (int i = 0; i < cuenta; i++) {
            datos[i] = actual.getDato();
            actual = actual.getDerecha();
        }
        return datos;
    }

    /** Recorrido de primero a ultimo usando las referencias derecha. */
    public String mostrarAdelante() {
        if (esVacia()) {
            return "(lista vacia)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = primero;
        int i = 0;
        while (actual != null) {
            sb.append(i).append(". ").append(actual.getDato()).append("\n");
            actual = actual.getDerecha();
            i++;
        }
        return sb.toString().trim();
    }

    /** Recorrido de ultimo a primero usando las referencias izquierda. */
    public String mostrarAtras() {
        if (esVacia()) {
            return "(lista vacia)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = ultimo;
        int i = cuenta - 1;
        while (actual != null) {
            sb.append(i).append(". ").append(actual.getDato()).append("\n");
            actual = actual.getIzquierda();
            i--;
        }
        return sb.toString().trim();
    }

    private void validarDato(Object dato) throws Exception {
        if (dato == null) {
            throw new Exception("El dato no puede ser nulo");
        }
    }
}
