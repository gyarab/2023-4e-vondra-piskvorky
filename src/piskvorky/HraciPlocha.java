
package piskvorky;

import java.util.ArrayList;

/**
 * Trida pro praci z hraci plochou
 * @author Tom
 */
public class HraciPlocha {

    public static final int PRAZDNE_POLE = -1;
    public static final int ROZMERX = 13;
    public static final int ROZMERY = 13;
    private int[][] stav = new int[ROZMERX][ROZMERY];
    private ArrayList<Tah> historie = new ArrayList<>();
    private int hracNaTahu;
    private boolean konec;
    

    /**
     * Pocet symbolu v rade znamenajici vitezstvi
     */
    private int pocetNaVitezstvi = 5;

    /**
     * Pocet soubezne hrajicich hracu
     */
    public static final int POCET_HRACU = 2;

    /**
     * Vrati pocet symbolu v rade znamenajici vitezstvi
     */
    public int getPocetNaVitezstvi() {
        return pocetNaVitezstvi;
    }

    /**
     * Vraci, ktery hrac je na tahu
     * @return
     */
    public int getHracNaTahu() {
        return hracNaTahu;
    }

    /**
     * Urcuje, zda na teto hraci plose bylo dohrano
     * @return
     */
    public boolean jeKonec() {
        return konec;
    }
    public void setKonec(boolean konec) {
        this.konec = konec;
    }

    /**
     * Udela kopii aktualniho stavu a vrati ji. Neda pristup k originalu
     * @return
     */
    public int[][] getStav() {
        int[][] copy = new int[stav.length][];

        for (int i = 0; i < stav.length; i++) {
            copy[i] = stav[i].clone();
        }
        return copy;
    }


    /**
     * Konstruktor pro hraci plochu s jeji inicializaci
     */
    public HraciPlocha(int pocetNaVitezstvi) {
        for (int x = 0; x < stav.length; x++) {
            for (int y = 0; y < stav.length; y++) {
                stav[x][y] = PRAZDNE_POLE; // nehrané pole
            }
        }
        this.pocetNaVitezstvi = pocetNaVitezstvi;
    }

    /**
     * Provede tah na hraci plose
     * @param tah
     * @return
     * @throws Exception
     */
    public boolean zahratTah(Tah tah) throws Exception {
        // zkontroluji, jestli je tah validní
        if (tah.x >= ROZMERX || tah.y >= ROZMERY || tah.x < 0 || tah.y < 0) {
            throw new Exception("Tah je mimo hrací plochu");
        }
        if (stav[tah.x][tah.y] != PRAZDNE_POLE) {
                throw new Exception("Na tomto poli již byl proveden tah");
        }
        if (konec) {
            throw new Exception("Hra skončila");
        }
        // vykreslím tah
        stav[tah.x][tah.y] = hracNaTahu;
        // zaznamenám si do historie tah
        historie.add(tah);
        // vyhodnotím stav po tahu
        if (vyhodnotit()) {
            // někdo vyhrál, nastavím konec
            return konec = true;
        }

        if (jeVyplnena(stav)) {
            konec = true;
            throw new Exception("Remíza");
        }

        // nikdo nevyhrál,nastavím si dalšího hráče
        hracNaTahu++;
        if (hracNaTahu >= POCET_HRACU) {
            hracNaTahu = 0;
        }
        return false;

    }

    private boolean vyhodnotit() {
        for (int i = 0; i < ROZMERX; i++) {
            for (int j = 0; j < ROZMERY; j++) {
                if (zkontrolujSmer(i, j, 1, 0)
                        || // Horizontálně
                        zkontrolujSmer(i, j, 0, 1)
                        || // Vertikálně
                        zkontrolujSmer(i, j, 1, 1)
                        || // Diagonálně
                        zkontrolujSmer(i, j, 1, -1)) {
                    // Anti-diagonálně
                    return true;
                }
            }
        }
        return false;
    }

    private boolean zkontrolujSmer(int x, int y, int deltaX, int deltaY) {

        int hrac = stav[x][y];
        if (hrac == PRAZDNE_POLE) {
            return false;
        }
        int count = 1; // Aktuální symbol

        // Kontrola v jednom směru
        for (int i = 1; i < pocetNaVitezstvi; i++) {
            int newX = x + i * deltaX;
            int newY = y + i * deltaY;

            if (newX >= 0 && newX < ROZMERX && newY >= 0 && newY < ROZMERY
                    && stav[newX][newY] == hrac) {
                count++;
            } else {
                break;
            }
        }

        return count >= pocetNaVitezstvi;
    }
    
    
    /**
     * Zjisti, zda je hraci plocha cela vyplnena
     * @param stav hraci plocha
     * @return vyplnenost
     */
    public static boolean jeVyplnena(int[][] stav) {
        for (int i = 0; i < ROZMERX; i++) {
            for (int j = 0; j < ROZMERY; j++) {
                if (stav[i][j] == PRAZDNE_POLE) {
                    return false;
                }
            }
        }

        return true;
    }

}
