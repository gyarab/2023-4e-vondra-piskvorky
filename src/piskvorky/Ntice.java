package piskvorky;

import java.util.ArrayList;

/**
 * Trida pro ukladani stavu Ntice
 */
public class Ntice {

    public Ntice(int hrac, Tah pocatek, int deltaX, int deltaY) {
        this.hrac = hrac;
        this.pocatek = pocatek;
        this.konec = pocatek;
        this.tahyKBlokaci = new ArrayList<Tah>();
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    private int velikost;
    /**
     * Velikost ntice
     * @return
     */
    public int getVelikost() {
        return velikost;
    }
    /**
     * Velikost ntice
     */
    public void setVelikost(int velikost) {
        this.velikost = velikost;
    }
    private int hrac;
    /**
     * Hrac, kteremu patri tato ntice
     * @return
     */
    public int getHrac() {
        return hrac;
    }
    private ArrayList<Tah> tahyKBlokaci;
    /**
     * Tahy, ktere mohou zablokovat ntici - jinak- volna pole okolo ntice
     * @return
     */
    public ArrayList<Tah> getTahyKBlokaci() {
        return tahyKBlokaci;
    }
    private Tah pocatek;
    /**
     * Pocatek ntice
     * @param pocatek
     */
    public void setPocatek(Tah pocatek) {
        this.pocatek = pocatek;
    }
    /**
     * Pocatek ntice
     */
    public Tah getPocatek() {
        return pocatek;
    }
    private Tah konec;
    /**
     * Konec ntice
     */
    public Tah getKonec() {
        return konec;
    }

    private int deltaX;
    /**
     * Smer vektoru v X
     */
    public int getDeltaX() {
        return deltaX;
    }

    private int deltaY;
    /**
     * Smer vektoru v Y
     */
    public int getDeltaY() {
        return deltaY;
    }
}
