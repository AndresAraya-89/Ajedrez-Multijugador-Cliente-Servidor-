package Logica;

import Entidades.Ficha;
import java.awt.GraphicsEnvironment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Andres Araya
 */
public class Ajedrez implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TIPO_VACIO = 0;
    public static final int TIPO_TORRE = 1;
    public static final int TIPO_CABALLO = 2;
    public static final int TIPO_ARFIL = 3;
    public static final int TIPO_REINA = 4;
    public static final int TIPO_REY = 5;
    public static final int TIPO_PEON = 6;

    public static final int ESTADO_NORMAL = 0;
    public static final int ESTADO_JAQUE = 1;
    public static final int ESTADO_JAQUE_MATE = 2;
    public static final int ESTADO_AHOGADO = 3;

    private int filas;
    private int columnas;
    private Ficha areaJuego[][];
    private Ficha atacante;
    private int jugador;
    private boolean inicioJuego;
    private int jugadorGanador;
    private int estadoJuego;

    public Ajedrez(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.areaJuego = new Ficha[this.filas][this.columnas];
        this.atacante = null;
        this.jugador = 1;
        this.jugadorGanador = 0;
        this.estadoJuego = ESTADO_NORMAL;
        agregarFichas();
    }

    public int getFilas() { return filas; }
    public void setFilas(int filas) { this.filas = filas; }
    public int getColumnas() { return columnas; }
    public void setColumnas(int columnas) { this.columnas = columnas; }
    public Ficha[][] getAreaJuego() { return areaJuego; }
    public void setAreaJuego(Ficha[][] areaJuego) { this.areaJuego = areaJuego; }
    public Ficha getAtacante() { return atacante; }
    public void setAtacante(Ficha atacante) { this.atacante = atacante; }
    public int getJugador() { return jugador; }
    public void setJugador(int jugador) { this.jugador = jugador; }
    public boolean isInicioJuego() { return inicioJuego; }
    public void setInicioJuego(boolean inicioJuego) { this.inicioJuego = inicioJuego; }
    public int getJugadorGanador() { return jugadorGanador; }
    public void setJugadorGanador(int jugadorGanador) { this.jugadorGanador = jugadorGanador; }
    public int getEstadoJuego() { return estadoJuego; }

    /**
     * Inicialización del tablero: piezas blancas (jugador 1) en las filas 0-1
     * y piezas negras (jugador 2) en las filas filas-2 y filas-1.
     */
    private void agregarFichas() {
        agregarEspaciosVacios();
        int[] filaTrasera = {
            TIPO_TORRE, TIPO_CABALLO, TIPO_ARFIL, TIPO_REINA,
            TIPO_REY, TIPO_ARFIL, TIPO_CABALLO, TIPO_TORRE
        };
        for (int j = 0; j < columnas; j++) {
            int tipoTrasera = filaTrasera[j % filaTrasera.length];
            areaJuego[0][j] = new Ficha(tipoTrasera, 0, j, 1);
            areaJuego[1][j] = new Ficha(TIPO_PEON, 1, j, 1);
            areaJuego[filas - 2][j] = new Ficha(TIPO_PEON, filas - 2, j, 2);
            areaJuego[filas - 1][j] = new Ficha(tipoTrasera, filas - 1, j, 2);
        }
    }

    public void agregarEspaciosVacios() {
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                areaJuego[i][j] = new Ficha(i, j);
            }
        }
    }

    public void anularFichasMarcadas() {
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                areaJuego[i][j].setFichaMarcada(false);
            }
        }
    }

    public void anularFichasPuedenSerComidas() {
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {
                areaJuego[i][j].setPuedeSerComida(false);
            }
        }
    }

    public boolean dentroDelTablero(int x, int y) {
        return x >= 0 && x < filas && y >= 0 && y < columnas;
    }

    public boolean hayDesborde(int x, int y) {
        return !dentroDelTablero(x, y);
    }

    /* ================================================================
     *  GENERACIÓN DE MOVIMIENTOS PSEUDOLEGALES
     *  (sin filtrar la regla de "no dejar al rey propio en jaque")
     * ================================================================ */

    public List<int[]> pseudolegales(Ficha p) {
        switch (p.getTipoFicha()) {
            case TIPO_PEON:    return pseudolegalesPeon(p);
            case TIPO_TORRE:   return pseudolegalesTorre(p);
            case TIPO_CABALLO: return pseudolegalesCaballo(p);
            case TIPO_ARFIL:   return pseudolegalesArfil(p);
            case TIPO_REINA:   return pseudolegalesReina(p);
            case TIPO_REY:     return pseudolegalesRey(p);
            default:           return new ArrayList<>();
        }
    }

    private List<int[]> pseudolegalesPeon(Ficha p) {
        List<int[]> r = new ArrayList<>();
        int x = p.getPosicionX();
        int y = p.getPosicionY();
        int dir = (p.getTipoJugador() == 1) ? 1 : -1;
        int filaInicial = (p.getTipoJugador() == 1) ? 1 : filas - 2;

        // Avance simple
        if (dentroDelTablero(x + dir, y) && areaJuego[x + dir][y].getTipoFicha() == TIPO_VACIO) {
            r.add(new int[]{x + dir, y});
            // Avance doble desde fila inicial, requiere ambas casillas vacías
            if (x == filaInicial
                    && dentroDelTablero(x + 2 * dir, y)
                    && areaJuego[x + 2 * dir][y].getTipoFicha() == TIPO_VACIO) {
                r.add(new int[]{x + 2 * dir, y});
            }
        }

        // Capturas diagonales y al paso
        for (int dy : new int[]{-1, 1}) {
            int nx = x + dir;
            int ny = y + dy;
            if (!dentroDelTablero(nx, ny)) continue;

            Ficha destino = areaJuego[nx][ny];
            if (destino.getTipoFicha() != TIPO_VACIO
                    && destino.getTipoJugador() != p.getTipoJugador()) {
                r.add(new int[]{nx, ny});
                continue;
            }

            // Captura al paso: peón enemigo lateral que acaba de avanzar 2
            if (destino.getTipoFicha() == TIPO_VACIO && dentroDelTablero(x, ny)) {
                Ficha lateral = areaJuego[x][ny];
                if (lateral.getTipoFicha() == TIPO_PEON
                        && lateral.getTipoJugador() != p.getTipoJugador()
                        && lateral.getPeonAvanzoDosCasillas()) {
                    r.add(new int[]{nx, ny});
                }
            }
        }
        return r;
    }

    private List<int[]> pseudolegalesTorre(Ficha p) {
        return generarLineas(p, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
    }

    private List<int[]> pseudolegalesArfil(Ficha p) {
        return generarLineas(p, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
    }

    private List<int[]> pseudolegalesReina(Ficha p) {
        List<int[]> r = pseudolegalesTorre(p);
        r.addAll(pseudolegalesArfil(p));
        return r;
    }

    private List<int[]> generarLineas(Ficha p, int[][] dirs) {
        List<int[]> r = new ArrayList<>();
        int x = p.getPosicionX();
        int y = p.getPosicionY();
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            while (dentroDelTablero(nx, ny)) {
                Ficha destino = areaJuego[nx][ny];
                if (destino.getTipoFicha() == TIPO_VACIO) {
                    r.add(new int[]{nx, ny});
                } else {
                    if (destino.getTipoJugador() != p.getTipoJugador()) {
                        r.add(new int[]{nx, ny});
                    }
                    break;
                }
                nx += d[0];
                ny += d[1];
            }
        }
        return r;
    }

    private List<int[]> pseudolegalesCaballo(Ficha p) {
        List<int[]> r = new ArrayList<>();
        int x = p.getPosicionX();
        int y = p.getPosicionY();
        int[][] off = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] o : off) {
            int nx = x + o[0];
            int ny = y + o[1];
            if (!dentroDelTablero(nx, ny)) continue;
            Ficha destino = areaJuego[nx][ny];
            if (destino.getTipoFicha() == TIPO_VACIO
                    || destino.getTipoJugador() != p.getTipoJugador()) {
                r.add(new int[]{nx, ny});
            }
        }
        return r;
    }

    private List<int[]> pseudolegalesRey(Ficha p) {
        List<int[]> r = new ArrayList<>();
        int x = p.getPosicionX();
        int y = p.getPosicionY();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (!dentroDelTablero(nx, ny)) continue;
                Ficha destino = areaJuego[nx][ny];
                if (destino.getTipoFicha() == TIPO_VACIO
                        || destino.getTipoJugador() != p.getTipoJugador()) {
                    r.add(new int[]{nx, ny});
                }
            }
        }
        // Enroque (libre albedrío: solo se exige que las piezas no se hayan
        // movido y que las casillas entre rey y torre estén vacías)
        if (!p.getHaMovido()) {
            if (puedeEnrocar(p, +1)) r.add(new int[]{x, y + 2});
            if (puedeEnrocar(p, -1)) r.add(new int[]{x, y - 2});
        }
        return r;
    }

    private boolean puedeEnrocar(Ficha rey, int direccion) {
        int x = rey.getPosicionX();
        int y = rey.getPosicionY();
        int yTorre = (direccion > 0) ? columnas - 1 : 0;
        if (!dentroDelTablero(x, yTorre)) return false;

        Ficha torre = areaJuego[x][yTorre];
        if (torre.getTipoFicha() != TIPO_TORRE) return false;
        if (torre.getTipoJugador() != rey.getTipoJugador()) return false;
        if (torre.getHaMovido()) return false;

        int desde = Math.min(y, yTorre) + 1;
        int hasta = Math.max(y, yTorre) - 1;
        for (int j = desde; j <= hasta; j++) {
            if (areaJuego[x][j].getTipoFicha() != TIPO_VACIO) return false;
        }
        return true;
    }

    /* ================================================================
     *  DETECCIÓN DE ATAQUES Y JAQUE
     * ================================================================ */

    /**
     * Devuelve true si alguna pieza del jugador 'jugadorAtacante' ataca
     * la casilla (x,y). Para el rey y el peón se usa lógica especial
     * para evitar recursión infinita con el enroque.
     */
    public boolean esCasillaAtacada(int x, int y, int jugadorAtacante) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Ficha p = areaJuego[i][j];
                if (p.getTipoFicha() == TIPO_VACIO) continue;
                if (p.getTipoJugador() != jugadorAtacante) continue;

                if (p.getTipoFicha() == TIPO_PEON) {
                    int dir = (p.getTipoJugador() == 1) ? 1 : -1;
                    if (i + dir == x && (j - 1 == y || j + 1 == y)) return true;
                } else if (p.getTipoFicha() == TIPO_REY) {
                    if (Math.abs(i - x) <= 1 && Math.abs(j - y) <= 1
                            && !(i == x && j == y)) return true;
                } else {
                    List<int[]> moves;
                    switch (p.getTipoFicha()) {
                        case TIPO_TORRE:   moves = pseudolegalesTorre(p); break;
                        case TIPO_CABALLO: moves = pseudolegalesCaballo(p); break;
                        case TIPO_ARFIL:   moves = pseudolegalesArfil(p); break;
                        case TIPO_REINA:   moves = pseudolegalesReina(p); break;
                        default:           moves = new ArrayList<>();
                    }
                    for (int[] m : moves) {
                        if (m[0] == x && m[1] == y) return true;
                    }
                }
            }
        }
        return false;
    }

    public Ficha encontrarRey(int jugador) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Ficha p = areaJuego[i][j];
                if (p.getTipoFicha() == TIPO_REY && p.getTipoJugador() == jugador) {
                    return p;
                }
            }
        }
        return null;
    }

    public boolean enJaque(int jugador) {
        Ficha rey = encontrarRey(jugador);
        if (rey == null) return false;
        int oponente = (jugador == 1) ? 2 : 1;
        return esCasillaAtacada(rey.getPosicionX(), rey.getPosicionY(), oponente);
    }

    /* ================================================================
     *  MARCADO DE MOVIMIENTOS PARA LA VISTA
     *
     *  Modo "libre albedrío": se marcan TODOS los movimientos pseudolegales
     *  (válidos según las reglas de cada pieza) sin filtrar los que dejarían
     *  al propio rey en jaque. El jugador es libre de cometer errores; la
     *  partida termina cuando un rey es realmente capturado.
     * ================================================================ */

    public void movimientos() {
        if (atacante == null) return;
        if (atacante.getTipoJugador() != jugador) return; // no es tu turno
        for (int[] m : pseudolegales(atacante)) {
            areaJuego[m[0]][m[1]].setPuedeSerComida(true);
        }
    }

    /* ================================================================
     *  EJECUCIÓN DEL MOVIMIENTO (incluye casos especiales)
     * ================================================================ */

    public void cambioFichas(Ficha destino) {
        int xOrig = atacante.getPosicionX();
        int yOrig = atacante.getPosicionY();
        int xDest = destino.getPosicionX();
        int yDest = destino.getPosicionY();

        // Limpiar la marca "avanzó dos casillas" en TODOS los peones del jugador
        // que va a mover (la oportunidad de captura al paso solo dura 1 turno).
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Ficha f = areaJuego[i][j];
                if (f.getTipoFicha() == TIPO_PEON
                        && f.getTipoJugador() == atacante.getTipoJugador()) {
                    f.setPeonAvanzoDosCasillas(false);
                }
            }
        }

        boolean esEnroqueCorto = atacante.getTipoFicha() == TIPO_REY && (yDest - yOrig) == 2;
        boolean esEnroqueLargo = atacante.getTipoFicha() == TIPO_REY && (yDest - yOrig) == -2;
        boolean esAlPaso = atacante.getTipoFicha() == TIPO_PEON
                && yOrig != yDest
                && areaJuego[xDest][yDest].getTipoFicha() == TIPO_VACIO;
        boolean esAvanceDoble = atacante.getTipoFicha() == TIPO_PEON
                && Math.abs(xDest - xOrig) == 2;

        // Vaciar origen
        areaJuego[xOrig][yOrig] = new Ficha(xOrig, yOrig);

        // Mover atacante
        atacante.setPosicionX(xDest);
        atacante.setPosicionY(yDest);
        atacante.setFichaMarcada(false);
        atacante.setHaMovido(true);
        atacante.colorFicha();
        areaJuego[xDest][yDest] = atacante;

        // Captura al paso: el peón capturado está en la fila de origen, columna destino
        if (esAlPaso) {
            areaJuego[xOrig][yDest] = new Ficha(xOrig, yDest);
        }

        // Enroque: mover la torre correspondiente
        if (esEnroqueCorto) {
            Ficha torre = areaJuego[xDest][columnas - 1];
            torre.setPosicionY(yDest - 1);
            torre.setHaMovido(true);
            torre.colorFicha();
            areaJuego[xDest][yDest - 1] = torre;
            areaJuego[xDest][columnas - 1] = new Ficha(xDest, columnas - 1);
        } else if (esEnroqueLargo) {
            Ficha torre = areaJuego[xDest][0];
            torre.setPosicionY(yDest + 1);
            torre.setHaMovido(true);
            torre.colorFicha();
            areaJuego[xDest][yDest + 1] = torre;
            areaJuego[xDest][0] = new Ficha(xDest, 0);
        }

        // Marcar avance doble (para habilitar al paso en el siguiente turno)
        if (esAvanceDoble) {
            atacante.setPeonAvanzoDosCasillas(true);
        }

        // Coronación
        if (atacante.getTipoFicha() == TIPO_PEON && coronacionPeon(atacante)) {
            int eleccion = elegirPiezaCoronacion();
            atacante.setTipoFicha(eleccion);
        }

        // Cambio de turno y actualización del estado del juego
        intercambioJugador();
        actualizarEstadoJuego();
    }

    public boolean coronacionPeon(Ficha peon) {
        int x = peon.getPosicionX();
        if (peon.getTipoJugador() == 1 && x == filas - 1) return true;
        if (peon.getTipoJugador() == 2 && x == 0) return true;
        return false;
    }

    private int elegirPiezaCoronacion() {
        if (GraphicsEnvironment.isHeadless()) return TIPO_REINA;
        String[] fichas = {"Torre", "Caballo", "Arfil", "Reina"};
        int opcion = JOptionPane.showOptionDialog(
                null, "Elige una pieza", "Coronación",
                0, JOptionPane.QUESTION_MESSAGE, null, fichas, "Reina");
        if (opcion < 0 || opcion > 3) opcion = 3;
        return opcion + 1; // 1=Torre, 2=Caballo, 3=Arfil, 4=Reina
    }

    public void intercambioJugador() {
        jugador = (jugador == 1) ? 2 : 1;
    }

    /**
     * En modo libre albedrío la partida termina cuando un rey es capturado
     * (desaparece del tablero). El indicador de jaque se mantiene como aviso
     * informativo, pero NO bloquea movimientos ni finaliza la partida.
     */
    public void actualizarEstadoJuego() {
        Ficha rey1 = encontrarRey(1);
        Ficha rey2 = encontrarRey(2);
        if (rey1 == null) {
            estadoJuego = ESTADO_JAQUE_MATE;
            jugadorGanador = 2;
        } else if (rey2 == null) {
            estadoJuego = ESTADO_JAQUE_MATE;
            jugadorGanador = 1;
        } else if (enJaque(jugador)) {
            estadoJuego = ESTADO_JAQUE;
        } else {
            estadoJuego = ESTADO_NORMAL;
        }
    }

    public boolean jaqueMate() {
        return estadoJuego == ESTADO_JAQUE_MATE;
    }

    public boolean reyAhogado() {
        return estadoJuego == ESTADO_AHOGADO;
    }

    public boolean hayJaque() {
        return estadoJuego == ESTADO_JAQUE;
    }

    public void imprimir() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print("[" + this.areaJuego[i][j] + "]");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        Ajedrez ajedrez = new Ajedrez(8, 8);
        ajedrez.imprimir();
    }
}
