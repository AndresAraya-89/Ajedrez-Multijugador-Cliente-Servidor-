# Ajedrez Multijugador Cliente-Servidor

Proyecto académico de **Programación II (2023)** — autor: Andrés Araya.

Juego de ajedrez multijugador 1 vs 1 escrito en Java + Swing. Las dos
instancias del programa se comunican por una tubería TCP usando sockets y
serialización de objetos: una hace de servidor, la otra de cliente. Cada
movimiento se sincroniza en tiempo real con la otra ventana mediante un hilo
dedicado de recepción.

---

## Arquitectura

```
┌─────────────────────────┐               ┌─────────────────────────┐
│   VistaAreaJuego (P1)   │               │   VistaAreaJuego (P2)   │
│   ┌──────────────────┐  │               │  ┌──────────────────┐   │
│   │     Ajedrez      │  │   Socket TCP  │  │     Ajedrez      │   │
│   │  (lógica + EDT)  │◄─┼───────────────┼─►│  (lógica + EDT)  │   │
│   └──────────────────┘  │  ObjectStream │  └──────────────────┘   │
│   Hilo de recepción     │  serializado  │   Hilo de recepción     │
└─────────────────────────┘               └─────────────────────────┘
```

- **Servidor** (`Jugador 1`, blancas): abre `ServerSocket` en el puerto 5000
  y bloquea en `accept()` dentro de un hilo dedicado para no congelar el EDT.
- **Cliente** (`Jugador 2`, negras): abre `Socket` apuntando a `localhost:5000`.
- Ambos lados envían el objeto `Ajedrez` completo tras cada jugada con
  `ObjectOutputStream.writeObject() + flush() + reset()`. El `reset()` evita
  que la JVM cachee la referencia y el peer reciba el mismo estado obsoleto.
- Un **hilo daemon** lee continuamente del socket; cuando llega un nuevo
  estado lo aplica al EDT mediante `SwingUtilities.invokeLater`, garantizando
  que ningún componente Swing se toque desde fuera del hilo de eventos.

### Estructura de paquetes

| Paquete       | Clases                                  | Responsabilidad                          |
|---------------|-----------------------------------------|------------------------------------------|
| `Entidades`   | `Ficha`                                  | Representa una pieza o casilla vacía     |
| `Logica`      | `Ajedrez`, `Sockets`                     | Reglas del juego y comunicación TCP      |
| `Vista`       | `VistaAreaJuego`, `VistaFicha`           | Ventana, tablero y celdas (Swing)        |
| `Imagenes`    | `xyz.png`                                | Sprites `tipoFicha + tipoJugador + color`|

### Codificación de piezas

| Código | Pieza   |
|:------:|---------|
|   0    | Vacío   |
|   1    | Torre   |
|   2    | Caballo |
|   3    | Arfil   |
|   4    | Reina   |
|   5    | Rey     |
|   6    | Peón    |

Cada imagen se nombra `<tipoFicha><tipoJugador><colorCasilla>.png` (por ejemplo
`520.png` = rey blanco sobre casilla clara).

---

## Funcionalidades

### Reglas de movimiento

Implementadas con generadores pseudolegales por pieza:

- **Peón**: avance simple, avance doble desde fila inicial (con casilla
  intermedia libre), captura diagonal, captura **al paso** y **coronación**
  con diálogo para elegir Torre / Caballo / Arfil / Reina.
- **Torre / Alfil / Reina**: deslizamiento por líneas y diagonales, se detiene
  en la primera pieza encontrada (capturable si es enemiga).
- **Caballo**: ocho saltos en L.
- **Rey**: ocho casillas adyacentes y **enroque** corto/largo.

### Modo libre albedrío

El motor **muestra todos los movimientos válidos** según las reglas de cada
pieza pero **no impide** al jugador hacer una jugada arriesgada que deje a su
rey expuesto. Esto significa que:

- Una pieza clavada (que protege al rey) puede moverse de todas formas.
- El rey puede ir a una casilla atacada.
- El enroque solo exige que rey y torre no se hayan movido y que las casillas
  entre ellos estén vacías.

El indicador `¡JAQUE!` aparece en la barra de título como aviso, pero **no
bloquea** ningún movimiento. La partida termina cuando un rey **es realmente
capturado**.

### Resaltado visual

Sobre el sprite de la pieza se dibuja un overlay semitransparente:

| Situación                        | Indicador                       |
|----------------------------------|---------------------------------|
| Pieza seleccionada               | Halo amarillo                   |
| Movimiento válido a casilla vacía| Círculo verde centrado          |
| Captura válida (pieza enemiga)   | Halo rojo + borde resaltado     |

El color base de la casilla se renderiza con código (beige claro / marrón
oscuro al estilo tablero clásico).

### Cronómetro

`javax.swing.Timer` que dispara cada segundo y actualiza un `JLabel` en formato
`MM:SS`:

- Arranca cuando se establece la conexión.
- Se detiene automáticamente al terminar la partida.
- Se reinicia a `00:00` cuando comienza la siguiente partida.

### Reinicio automático

Cuando un rey es capturado (o hay ahogado), se muestra un diálogo con el
resultado:

- `¡Has ganado!`
- `Has perdido. Ganó el Jugador X.`
- `Empate.`

Tras cerrarlo, **la partida se reinicia automáticamente** en ambos lados. Como
`new Ajedrez(8,8)` es determinista, no se necesita resincronizar por socket —
cada cliente regenera localmente la posición inicial idéntica.

---

## Cómo ejecutar

Requisitos: **Java 1.8** o superior. El proyecto está en formato NetBeans
(`build.xml` con Ant), pero también compila a línea de comandos.

### Compilación

```bash
mkdir -p build/classes
javac -d build/classes -sourcepath src \
    src/Entidades/Ficha.java \
    src/Logica/Ajedrez.java \
    src/Logica/Sockets.java \
    src/Vista/VistaFicha.java \
    src/Vista/VistaAreaJuego.java
```

### Ejecución

Abrir **dos instancias** de la aplicación (en la misma máquina o en dos
terminales distintas):

```bash
# Terminal 1 (y Terminal 2)
java -cp build/classes Vista.VistaAreaJuego
```

En la primera ventana hacer clic en `Jugador 1` (servidor); en la segunda,
clic en `Jugador 2` (cliente).

---

## Flujo de juego

### Paso 1 — Configuración de roles y conexión

Al iniciar la aplicación se abren dos ventanas idénticas. En la parte superior
están los botones `Jugador 1` (servidor) y `Jugador 2` (cliente).

1. En la primera ventana hacer clic en `Jugador 1`. La aplicación abrirá el
   `ServerSocket` y mostrará "esperando conexión..." en el título.
2. En la segunda ventana hacer clic en `Jugador 2`. La aplicación se conectará
   al servidor y ambas ventanas mostrarán el cronómetro arrancado en `00:00`.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/7d3bd53d-5d5c-46b5-8eea-05263ccbdb2e" />

### Paso 2 — Primer movimiento (Jugador 1)

El Jugador 1 controla las **piezas blancas** (filas superiores). Hace clic en
una pieza propia → la pieza se resalta en amarillo y los destinos válidos se
marcan con círculos verdes (casillas vacías) o halos rojos (capturas). Hace
clic en un destino marcado → la pieza se mueve y el estado se sincroniza
inmediatamente en la ventana del Jugador 2.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/b6cc632d-ecd1-41e8-8304-b2f1858c1071" />

### Paso 3 — Sincronización en tiempo real (Jugador 2)

El Jugador 2 controla las **piezas negras**. Su título cambia a "Tu turno"
una vez que el Jugador 1 ha movido. Si un movimiento deja a su rey en jaque,
aparece el prefijo `¡JAQUE!` como aviso (informativo, no bloquea).

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/7f7e4eb0-a7eb-47c2-9ffd-a96b4f54b8c8" />

### Paso 4 — Fin de partida y reinicio

Los turnos alternan hasta que un rey es capturado. Aparece el diálogo de
resultado y, al cerrarlo, el tablero se reinicia automáticamente en ambos
lados conservando el emparejamiento y la conexión.

<img width="350" height="250" alt="image" src="https://github.com/user-attachments/assets/4cd41423-cb07-4d2c-8797-bae9c893fc1b" />

---

## Detalles técnicos relevantes

- **`Sockets.java`**: el `ObjectOutputStream` se construye **antes** que el
  `ObjectInputStream` en cada lado para evitar deadlock — el constructor de
  `OOS` envía la cabecera del stream que el `OIS` del peer necesita leer.
- **`Ajedrez.java`**: implementa `Serializable` con `serialVersionUID`. Toda
  la lógica está aquí — la vista no contiene reglas de ajedrez.
- **`Ficha.java`**: además de tipo, posición y jugador guarda dos flags
  importantes: `haMovido` (para enroque) y `peonAvanzoDosCasillas` (para
  habilitar la captura al paso del oponente).
- **Threading**: el hilo de recepción es daemon. El acceso a `ajedrez` se hace
  exclusivamente desde el EDT (lecturas en `mouseClicked`, escrituras a través
  de `SwingUtilities.invokeLater`). El método `Sockets.enviar` es
  `synchronized`.
