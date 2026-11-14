/**
 * Entidad del caso 3 (Biblioteca): una solicitud de prestamo de un libro.
 *
 * Identificador principal: codigoPrestamo (equals y hashCode comparan solo este campo).
 *
 * Estados posibles:
 *   PENDIENTE -> esta en la cola esperando ser entregado al usuario
 *   ENTREGADO -> el bibliotecario ya entrego el libro (esta en la pila de historial)
 *   CANCELADO -> se cancelo mientras estaba pendiente
 */
public class LibroPrestamo {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String CANCELADO = "CANCELADO";

    private String codigoPrestamo;
    private String tituloLibro;
    private String autor;
    private String nombreUsuario;
    private int diasPrestamo;
    private String estado;

    public LibroPrestamo(String codigoPrestamo, String tituloLibro, String autor,
                         String nombreUsuario, int diasPrestamo) {
        setCodigoPrestamo(codigoPrestamo);
        this.tituloLibro = tituloLibro;
        this.autor = autor;
        this.nombreUsuario = nombreUsuario;
        this.diasPrestamo = diasPrestamo;
        this.estado = PENDIENTE;
    }

    /** Crea un objeto solo con el codigo, util para buscar con equals(). */
    public static LibroPrestamo conCodigo(String codigoPrestamo) {
        return new LibroPrestamo(codigoPrestamo, "", "", "", 0);
    }

    public String getCodigoPrestamo() {
        return codigoPrestamo;
    }

    /** El codigo se guarda en mayusculas y sin espacios: "pr-001" y "PR-001" son el mismo. */
    public void setCodigoPrestamo(String codigoPrestamo) {
        this.codigoPrestamo = codigoPrestamo == null ? "" : codigoPrestamo.trim().toUpperCase();
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getDiasPrestamo() {
        return diasPrestamo;
    }

    public void setDiasPrestamo(int diasPrestamo) {
        this.diasPrestamo = diasPrestamo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** Dos prestamos son iguales si tienen el mismo codigo de prestamo. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof LibroPrestamo)) {
            return false;
        }
        LibroPrestamo that = (LibroPrestamo) otro;
        return codigoPrestamo.equals(that.codigoPrestamo);
    }

    @Override
    public int hashCode() {
        return codigoPrestamo.hashCode();
    }

    @Override
    public String toString() {
        return "[" + codigoPrestamo + "] " + tituloLibro + " (" + autor + ")"
                + " | Usuario: " + nombreUsuario
                + " | " + diasPrestamo + " dias"
                + " | " + estado;
    }
}
