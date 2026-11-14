# Sistema de préstamos de una biblioteca

Actividad de la Unidad 2 de **Estructuras de Datos** (Universidad de Cartagena): sistema de gestión
con **Lista, Cola y Pila** implementadas con nodos doblemente enlazados.
Caso asignado: **3 – Biblioteca**, entidad `LibroPrestamo`, identificador **código de préstamo**.

**Autor:** José Luis Arteta Buelvas

## Qué hace

Una biblioteca recibe solicitudes de préstamo de libros. Cada solicitud:

1. Se **registra** en la lista general (registro completo del sistema).
2. Queda **pendiente** en una cola, en orden de llegada.
3. Se **procesa** (el bibliotecario entrega el libro) en orden FIFO y pasa a la pila de historial.
4. Puede **cancelarse** mientras está pendiente (usando una cola auxiliar).
5. La última entrega puede **deshacerse**: sale de la pila y vuelve a la cola como pendiente.

| Estructura | Clase | Uso en el sistema |
|---|---|---|
| Lista | `Lista` | Registro de todos los préstamos, en cualquier estado |
| Cola | `Cola` | Préstamos pendientes por entregar (FIFO) |
| Pila | `Pila` | Historial de entregas; la cima es la más reciente (LIFO) |

## Estructura

```
src/
  Nodo.java               Nodo con dato y referencias izquierda / derecha
  Lista.java              Lista doblemente enlazada
  Cola.java               Cola con frente y fin
  Pila.java               Pila con cima
  LibroPrestamo.java      Entidad del caso (equals por código de préstamo)
  SistemaBiblioteca.java  Lógica del sistema y menú de consola
test/
  PruebasBiblioteca.java  Casos de prueba sin librerías externas
```

## Cómo ejecutar

Requisitos: JDK 17 o superior. En PowerShell, desde la carpeta del proyecto:

```powershell
# Si java no está en el PATH
$env:JAVA_HOME="$env:USERPROFILE\.jdks\ms-17.0.20.1"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

# Compilar
javac -encoding UTF-8 -d out src\*.java test\*.java

# Menú del sistema
java -cp out SistemaBiblioteca

# Casos de prueba
java -cp out PruebasBiblioteca
```

## Menú

```
 1. Registrar prestamo
 2. Ver todos los prestamos registrados
 3. Ver prestamos pendientes
 4. Procesar siguiente prestamo (entregar libro)
 5. Ver historial de prestamos entregados
 6. Buscar prestamo por codigo
 7. Cancelar prestamo pendiente
 8. Deshacer ultima entrega
 9. Ver cantidad de prestamos
10. Salir
```

## Validaciones

- Código obligatorio, de 3 a 15 letras, números o guiones, sin repetirse (`pr-001` y `PR-001` son el mismo).
- Título, autor y usuario obligatorios; días de préstamo entre 1 y 30.
- Mensajes de error al procesar sin pendientes, deshacer sin historial, cancelar un préstamo que
  no está pendiente o buscar un código inexistente.
