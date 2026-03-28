package Entidades;

import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Andres Araya
 */
public class Ficha implements Serializable {

    private int tipoFicha;
    private int posicionX;
    private int posicionY;
    private int tipoJugador;
    private int color;
    private boolean primerMovimiento;
    private boolean fichaMarcada;
    private boolean puedeSerComida;

    public Ficha(int posicionX, int posicionY) {
        this.tipoFicha = 0;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.tipoJugador = 0;
        this.primerMovimiento = false;
        this.fichaMarcada = false;
        this.puedeSerComida = false;
        colorFicha();
    }

    public Ficha(int tipoFicha, int posicionX, int posicionY, int tipoJugador) {
        this.tipoFicha = tipoFicha;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.tipoJugador = tipoJugador;
        this.primerMovimiento = false;
        this.fichaMarcada = false;
        this.puedeSerComida = false;
        colorFicha();
    }

    public int getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(int tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(int posicionX) {
        this.posicionX = posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }

    public void setPosicionY(int posicionY) {
        this.posicionY = posicionY;
    }

    public int getTipoJugador() {
        return tipoJugador;
    }

    public void setTipoJugador(int tipoJugador) {
        this.tipoJugador = tipoJugador;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getPrimerMovimiento() {
        return primerMovimiento;
    }

    public void setPrimerMovimiento(boolean primerMovimiento) {
        this.primerMovimiento = primerMovimiento;
    }

    public boolean getFichaMarcada() {
        return fichaMarcada;
    }

    public void setFichaMarcada(boolean fichaMarcada) {
        this.fichaMarcada = fichaMarcada;
    }

    public boolean getPuedeSerComida() {
        return puedeSerComida;
    }

    public void setPuedeSerComida(boolean puedeSerComida) {
        this.puedeSerComida = puedeSerComida;
    }

    public void colorFicha() {
        int x = getPosicionX();
        int y = getPosicionY();

        if ((x % 2 == 0 && y % 2 == 0) || (x % 2 != 0 && y % 2 != 0)) {
            setColor(0);
        } else {
            setColor(1);
        }
    }

    @Override
    public String toString() {
        return "F[ tF " + tipoFicha + ", pX " + posicionX + ", pY " + posicionY + ", tJ " + tipoJugador + ", c " + color + ", fM " + fichaMarcada + ", sC " + puedeSerComida + " ]";
    }

    public static void main(String[] args) {
        Ficha f = new Ficha(0, 1, 1, 0);
        System.out.println(f);
    }

}
