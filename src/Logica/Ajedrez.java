/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import Entidades.Ficha;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Andres Araya
 */
public class Ajedrez implements Serializable {

    private int filas;
    private int columnas;
    private Ficha areaJuego[][];
    private int idFicha;
    private int numeroFicha;
    private boolean estadoFicha;
    private Ficha atacante;
    private int jugador;
    private boolean inicioJuego;
    private int jugadorGanador;

    public Ajedrez(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.areaJuego = new Ficha[this.filas][this.columnas];
        this.idFicha = 1;
        this.numeroFicha = 3;
        this.estadoFicha = true;
        this.atacante = null;
        this.jugador = 1;
        this.jugadorGanador = 0;
        agregarFichas();
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public Ficha[][] getAreaJuego() {
        return areaJuego;
    }

    public void setAreaJuego(Ficha[][] areaJuego) {
        this.areaJuego = areaJuego;
    }

    public int getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(int idFicha) {
        this.idFicha = idFicha;
    }

    public boolean getEstadoFicha() {
        return estadoFicha;
    }

    public void setEstadoFicha(boolean estadoFicha) {
        this.estadoFicha = estadoFicha;
    }

    public int getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(int numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Ficha getAtacante() {
        return atacante;
    }

    public void setAtacante(Ficha atacante) {
        this.atacante = atacante;
    }

    public int getJugador() {
        return jugador;
    }

    public void setJugador(int jugador) {
        this.jugador = jugador;
    }

    public boolean isInicioJuego() {
        return inicioJuego;
    }

    public void setInicioJuego(boolean inicioJuego) {
        this.inicioJuego = inicioJuego;
    }

    public int getJugadorGanador() {
        return jugadorGanador;
    }

    public void setJugadorGanador(int jugadorGanador) {
        this.jugadorGanador = jugadorGanador;
    }

    public void controladorIdFicha() {

        for (int i = 0; i < this.columnas; i++) {

            if (getIdFicha() == i) {
                if ((i < 5) && getEstadoFicha()) {
                    setIdFicha(i + 1);

                } else {
                    setEstadoFicha(false);
                    setIdFicha(getNumeroFicha());
                    setNumeroFicha(getNumeroFicha() - 1);
                }

                break;
            }
        }
    }

    public void agregarFilaElite(int fila, int columnas) {

        for (int j = 0; j < this.columnas; j++) {

            if (fila == 0) {
                areaJuego[fila][j] = new Ficha(getIdFicha(), fila, j, 1);
            } else if (fila == (columnas - 1)) {

                areaJuego[fila][j] = new Ficha(getIdFicha(), fila, j, 2);
            }

            controladorIdFicha();
        }

        setIdFicha(1);
        setNumeroFicha(3);
        setEstadoFicha(true);
    }

    public void agregarFilaPeones(int fila, int columnas) {

        for (int j = 0; j < this.columnas; j++) {

            if (fila == 1) {
                areaJuego[fila][j] = new Ficha(6, fila, j, 1);
            } else if (fila == (columnas - 2)) {

                areaJuego[fila][j] = new Ficha(6, fila, j, 2);
            }
        }

    }

    public void agregarEspaciosVacios() {
        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < (this.columnas); j++) {
                areaJuego[i][j] = new Ficha(0, i, j, 0);
            }
        }
    }

    private void agregarFichas() {
        agregarEspaciosVacios();
        agregarFilaElite(0, columnas);
        agregarFilaPeones(1, columnas);
        agregarFilaPeones(filas - 2, columnas);
        agregarFilaElite(filas - 1, columnas);
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

    /**
     * MOVIMIENTOS
     */
    public void movimientos() {

        if (getAtacante().getTipoFicha() == 6) {
            movimiemtoPeon();
        } else if (getAtacante().getTipoFicha() == 1) {
            movimientoTorre();
        } else if (getAtacante().getTipoFicha() == 2) {
            movimientoCaballo();
        } else if (getAtacante().getTipoFicha() == 3) {
            movimientoArfil();
        } else if (getAtacante().getTipoFicha() == 4) {
            movimientoReina();
        } else if (getAtacante().getTipoFicha() == 5) {
            movimientoRey();
        }
    }

    public boolean peonConPaso(int numero) {
        boolean tienePaso = false;
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();

        if (getAtacante().getTipoJugador() == 1) {
            if (areaJuego[x + numero][y].getTipoFicha() == 0) {
                tienePaso = true;
            }
        } else if (getAtacante().getTipoJugador() == 2) {
            if (areaJuego[x - numero][y].getTipoFicha() == 0) {
                tienePaso = true;
            }
        }
        return tienePaso;
    }

    public void movimiemtoPeon() {

        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();
        int numeroSumar = 1;
        comerConPeon();

        if (!getAtacante().getPrimerMovimiento()) {
            if (peonConPaso(2)) {
                numeroSumar = 2;
            }
            getAtacante().setPrimerMovimiento(true);
        }

        if (getAtacante().getTipoJugador() == 1) {

            if (peonConPaso(1)) {
                areaJuego[x + numeroSumar][y].setPuedeSerComida(true);
                if (numeroSumar == 2) {
                    areaJuego[x + 1][y].setPuedeSerComida(true);
                }
            }
        } else {
            if (peonConPaso(1)) {
                areaJuego[x + -numeroSumar][y].setPuedeSerComida(true);
            }
        }
    }

    public boolean coronacionPeon(Ficha peon) {
        boolean cambio = false;
        int x = peon.getPosicionX();

        if (peon.getTipoJugador() == 1) {
            if (x == 7) {
                cambio = true;
            }
        } else if (peon.getTipoJugador() == 2) {
            if (x == 0) {
                cambio = true;
            }
        }

        return cambio;
    }

    public void comerConPeon() {
        int x, y;
        if (getAtacante().getTipoJugador() == 1) {
            x = getAtacante().getPosicionX() + 1;
            y = getAtacante().getPosicionY() + 1;
            if (!hayDesborde(x, y)) {
                if (areaJuego[x][y].getTipoJugador() == 2) {
                    areaJuego[x][y].setPuedeSerComida(true);
                }
            }

            x = getAtacante().getPosicionX() + 1;
            y = getAtacante().getPosicionY() - 1;
            if (!hayDesborde(x, y)) {
                if (areaJuego[x][y].getTipoJugador() == 2) {
                    areaJuego[x][y].setPuedeSerComida(true);
                }
            }

        } else if (getAtacante().getTipoJugador() == 2) {
            x = getAtacante().getPosicionX() - 1;
            y = getAtacante().getPosicionY() - 1;
            if (!hayDesborde(x, y)) {
                if (areaJuego[x][y].getTipoJugador() == 1) {
                    areaJuego[x][y].setPuedeSerComida(true);
                }
            }

            x = getAtacante().getPosicionX() - 1;
            y = getAtacante().getPosicionY() + 1;
            if (!hayDesborde(x, y)) {
                if (areaJuego[x][y].getTipoJugador() == 1) {
                    areaJuego[x][y].setPuedeSerComida(true);
                }
            }
        }
    }

    public void movimientoTorre() {
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();
        int contador = 0;
        for (int i = x; i < this.filas; i++) {
            if (getAtacante().getTipoJugador() != areaJuego[i][y].getTipoJugador()) {
                if (areaJuego[i][y].getTipoFicha() == 0) {
                    areaJuego[i][y].setPuedeSerComida(true);
                } else {
                    areaJuego[i][y].setPuedeSerComida(true);
                    break;
                }
            } else {
                contador++;
                if (contador == 2) {
                    break;
                }
            }
        }
        contador = 0;
        for (int i = x; i > -1; i--) {
            if (getAtacante().getTipoJugador() != areaJuego[i][y].getTipoJugador()) {
                if (areaJuego[i][y].getTipoFicha() == 0) {
                    areaJuego[i][y].setPuedeSerComida(true);
                } else {
                    areaJuego[i][y].setPuedeSerComida(true);
                    break;
                }
            } else {
                contador++;
                if (contador == 2) {
                    break;
                }
            }
        }
        contador = 0;
        for (int j = y; j < columnas; j++) {
            if (getAtacante().getTipoJugador() != areaJuego[x][j].getTipoJugador()) {
                if (areaJuego[x][j].getTipoFicha() == 0) {
                    areaJuego[x][j].setPuedeSerComida(true);
                } else {
                    if (areaJuego[x][j].getTipoJugador() != getAtacante().getTipoJugador()) {
                        System.out.println("Es distinto");
                        areaJuego[x][j].setPuedeSerComida(true);
                    } else {
                        System.out.println("son iguales");
                    }
                    break;
                }
            } else {
                contador++;
                if (contador == 2) {
                    break;
                }
            }
        }
        contador = 0;
        for (int j = y; j > -1; j--) {
            if (getAtacante().getTipoJugador() != areaJuego[x][j].getTipoJugador()) {
                if (areaJuego[x][j].getTipoFicha() == 0) {
                    areaJuego[x][j].setPuedeSerComida(true);
                } else {
                    if (areaJuego[x][j].getTipoJugador() != getAtacante().getTipoJugador()) {
                        System.out.println("Es distinto");
                        areaJuego[x][j].setPuedeSerComida(true);
                    } else {
                        System.out.println("son iguales");
                    }
                    break;
                }
            } else {
                contador++;
                if (contador == 2) {
                    break;
                }
            }
        }
    }

    public void movimientoCaballo() {
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();

        //Verticales
        if (!hayDesborde(x + 2, y - 1)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 2][y - 1].getTipoJugador()) {
                areaJuego[x + 2][y - 1].setPuedeSerComida(true);
                System.out.println(x + 2 + " --- " + (y - 1) + "JS");
            }
        }

        if (!hayDesborde(x + 2, y + 1)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 2][y + 1].getTipoJugador()) {
                areaJuego[x + 2][y + 1].setPuedeSerComida(true);
                System.out.println(x + 2 + " --- " + (y + 1));
            }
        }

        if (!hayDesborde(x - 2, y - 1)) {
            if (getAtacante().getTipoJugador() != areaJuego[x - 2][y - 1].getTipoJugador()) {
                areaJuego[x - 2][y - 1].setPuedeSerComida(true);
                System.out.println(x - 2 + " --- " + (y - 1));
            }
        }

        if (!hayDesborde(x - 2, y + 1)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 2][y - 1].getTipoJugador()) {
                areaJuego[x - 2][y + 1].setPuedeSerComida(true);
                System.out.println(x - 2 + " --- " + (y + 1));
            }
        }

        //Horizontales
        if (!hayDesborde(x + 1, y - 2)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 2][y - 1].getTipoJugador()) {
                areaJuego[x + 1][y - 2].setPuedeSerComida(true);
                System.out.println(x + 1 + " --- " + (y - 2));
            }
        }

        if (!hayDesborde(x - 1, y - 2)) {
            if (getAtacante().getTipoJugador() != areaJuego[x - 2][y - 1].getTipoJugador()) {
                areaJuego[x - 1][y - 2].setPuedeSerComida(true);
                System.out.println(x - 1 + " --- " + (y - 2));
            }
        }

        if (!hayDesborde(x + 1, y + 2)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 2][y + 1].getTipoJugador()) {
                areaJuego[x + 1][y + 2].setPuedeSerComida(true);
                System.out.println(x - 1 + " --- " + y + 2);
            }
        }

        if (!hayDesborde(x - 1, y + 2)) {
            if (getAtacante().getTipoJugador() != areaJuego[x + 1][y - 2].getTipoJugador()) {
                areaJuego[x - 1][y + 2].setPuedeSerComida(true);
                System.out.println(x + 1 + " --- " + y + 2);
            }
        }

    }

    public void movimientoArfil() {
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();
        int contadorX = x;
        int contadorY = y;

        while (!hayDesborde(contadorX + 1, contadorY + 1)) {
            contadorX++;
            contadorY++;
            if (getAtacante().getTipoJugador() != areaJuego[contadorX][contadorY].getTipoJugador()) {
                if (areaJuego[contadorX][contadorY].getTipoFicha() == 0) {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                } else {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                    break;
                }
            } else {
                break;
            }
        }
        contadorX = x;
        contadorY = y;
        while (!hayDesborde(contadorX - 1, contadorY - 1)) {
            contadorX--;
            contadorY--;
            if (getAtacante().getTipoJugador() != areaJuego[contadorX][contadorY].getTipoJugador()) {
                if (areaJuego[contadorX][contadorY].getTipoFicha() == 0) {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                } else {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                    break;
                }
            } else {
                break;
            }
        }
        contadorX = x;
        contadorY = y;
        while (!hayDesborde(contadorX + 1, contadorY - 1)) {
            contadorX++;
            contadorY--;
            if (getAtacante().getTipoJugador() != areaJuego[contadorX][contadorY].getTipoJugador()) {
                if (areaJuego[contadorX][contadorY].getTipoFicha() == 0) {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                } else {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                    break;
                }
            } else {
                break;
            }
        }
        contadorX = x;
        contadorY = y;
        while (!hayDesborde(contadorX - 1, contadorY + 1)) {
            contadorX--;
            contadorY++;
            if (getAtacante().getTipoJugador() != areaJuego[contadorX][contadorY].getTipoJugador()) {
                if (areaJuego[contadorX][contadorY].getTipoFicha() == 0) {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                } else {
                    areaJuego[contadorX][contadorY].setPuedeSerComida(true);
                    break;
                }
            } else {
                break;
            }
        }

    }

    public void movimientoReina() {
        movimientoTorre();
        movimientoArfil();
    }

    public void movimientoRey() {
        movimientoReyEsquinas();
        movimientoReyLados();

    }

    public void movimientoReyEsquinas() {
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();

        if (!hayDesborde(x + 1, y + 1)) {
            if (areaJuego[x + 1][y + 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x + 1][y + 1].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x - 1, y + 1)) {
            if (areaJuego[x - 1][y + 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x - 1][y + 1].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x - 1, y - 1)) {
            if (areaJuego[x - 1][y - 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x - 1][y - 1].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x + 1, y - 1)) {
            if (areaJuego[x + 1][y - 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x + 1][y - 1].setPuedeSerComida(true);
            }
        }
    }

    public void movimientoReyLados() {
        int x = getAtacante().getPosicionX();
        int y = getAtacante().getPosicionY();

        if (!hayDesborde(x, y + 1)) {
            if (areaJuego[x][y + 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x][y + 1].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x, y - 1)) {
            if (areaJuego[x][y - 1].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x][y - 1].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x + 1, y)) {
            if (areaJuego[x + 1][y].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x + 1][y].setPuedeSerComida(true);
            }
        }

        if (!hayDesborde(x - 1, y)) {
            if (areaJuego[x - 1][y].getTipoJugador() != getAtacante().getTipoJugador()) {
                areaJuego[x - 1][y].setPuedeSerComida(true);
            }
        }
    }

    public boolean hayDesborde(int x, int y) {
        boolean seDesborda = false;

        if (x < 0 || x > (getFilas() - 1)) {
            seDesborda = true;
        }

        if (y < 0 || y > (getColumnas() - 1)) {
            seDesborda = true;
        }

        return seDesborda;
    }

    public void cambioFichas(Ficha ficha) {

        int x = ficha.getPosicionX();
        int y = ficha.getPosicionY();

        Ficha fichaVacia = new Ficha(getAtacante().getPosicionX(), getAtacante().getPosicionY());
        areaJuego[getAtacante().getPosicionX()][getAtacante().getPosicionY()] = fichaVacia;

        getAtacante().setPosicionX(x);
        getAtacante().setPosicionY(y);
        getAtacante().setFichaMarcada(false);
        getAtacante().colorFicha();
        areaJuego[x][y] = getAtacante();

        if (areaJuego[x][y].getTipoFicha() == 6) {
            if (coronacionPeon(areaJuego[x][y])) {
                String fichas[] = {"Torre", "Caballo", "Arfil", "Reina"};
                int opcion = JOptionPane.showOptionDialog(null, "Elige una ficha", "Coronacion", 0, JOptionPane.QUESTION_MESSAGE, null, fichas, "Reina");
                areaJuego[x][y].setTipoFicha(opcion + 1);
            }

        }
    }

    public void intercambioJugador() {
        if (getJugador() == 1) {
            setJugador(2);
        } else if (getJugador() == 2) {
            setJugador(1);
        }
    }

    public boolean jaqueMate() {
        ArrayList<Ficha> reyes = new ArrayList<>();
        boolean salida = false;

        for (int i = 0; i < this.filas; i++) {
            for (int j = 0; j < this.columnas; j++) {

                if (areaJuego[i][j].getTipoFicha() == 5) {
                    reyes.add(areaJuego[i][j]);
                }
            }
        }

        if (reyes.size() == 1) {
            salida = true;
            setJugadorGanador(reyes.get(1).getTipoJugador());
        }

        return salida;
    }

    //imprimir
    public void imprimir() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print("[" + this.areaJuego[i][j] + "]");
            }
            System.out.println("");
            System.out.println("");
            System.out.println("");

        }
    }

    public static void main(String[] args) {
        Ajedrez ajedrez = new Ajedrez(8, 8);

        ajedrez.imprimir();

    }

}