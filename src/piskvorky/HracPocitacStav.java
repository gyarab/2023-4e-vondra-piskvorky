package piskvorky;

import static piskvorky.HraciPlocha.POCET_HRACU;
import static piskvorky.HraciPlocha.PRAZDNE_POLE;
import static piskvorky.HracPocitac.MAXIMALNI_HLOUBKA;
import static piskvorky.HraciPlocha.ROZMERX;
import static piskvorky.HraciPlocha.ROZMERY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Trida udrzuje aktualni stav informaci pro vypocet tahu
 */
public class HracPocitacStav {
    /**
     * Konstruktor
     * 
     * @param hraciPlocha
     */
    public HracPocitacStav(HraciPlocha hraciPlocha) {
        this.hraciPlocha = hraciPlocha;
        this.stavHraciPlochy = hraciPlocha.getStav();
        this.potencionalniTahy = najdiHratelnaPole(hraciPlocha.getStav());
    }

    private final static int POTENCIALNE_HRATELNE_POLE = -2;
    private HraciPlocha hraciPlocha;
    private int[][] stavHraciPlochy;
    ArrayList<Tah> potencionalniTahy;

    /**
     * Vrati spocitane potencionalni tahy, ktere je potreba prozkoumat
     * 
     * @return
     */
    public ArrayList<Tah> getPotencionalniTahy() {
        return potencionalniTahy;
    }

    /**
     * Hraci plocha po provedeni zkoumanych tahu
     * 
     * @return
     */
    public int[][] getStavHraciPlochy() {
        return stavHraciPlochy;
    }

    /**
     * Vyhodnoti aktualni stav a prideli hodnotu podle vyhodnost
     * 
     * @param stav              aktualni stav hraci plochy
     * @param potencionalniTahy potencionalni tahy k dispozici
     * @return hodnota vyhodnosti
     */
    public Dvojice<Integer, ArrayList<Ntice>> vyhodnotitPodleCetnosti(int cisloPocitace) {
        // Spocitat cetnosti a ntice
        Dvojice<int[][][], ArrayList<Ntice>> nticeTuple = spoctiCetnosti();
        int[][][] cetnosti = nticeTuple.getPrvni();

        // zkontroluju, jestli jsem prohral/vyhral
        for (int hrac = 0; hrac < cetnosti.length; hrac++) {
            if (cetnosti[hrac][hraciPlocha.getPocetNaVitezstvi() - 1][0] > 0) {
                return new Dvojice<Integer, ArrayList<Ntice>>(
                        hrac == cisloPocitace /* Jsem to ja? */ ? Integer.MAX_VALUE : Integer.MIN_VALUE,
                        nticeTuple.getDruhy());
            }
        }

        int score = 0;

        // Udelam vazeny soucet cetnosti, kde moje se budou pricitat a souperova cetnost
        // se bude odecitat
        // a aby mela vetsi vahu delsi sekvence, tak vynasobim delkou sekvence
        for (int hrac = 0; hrac < cetnosti.length; hrac++) { // iterace pres hrace
            int jsemToJaKoef = hrac == cisloPocitace /* Jsem to ja? */ ? 1 : -1;
            for (int ntice = 0; ntice < cetnosti[hrac].length; ntice++) { // iterace pres ntice
                // Nezablokovane pocitam 2x
                score += cetnosti[hrac][ntice][0] * 2 * ntice * jsemToJaKoef;
                // Jednou zablokovane jednou
                score += cetnosti[hrac][ntice][1] * ntice * jsemToJaKoef;
            }
        }
        return new Dvojice<Integer, ArrayList<Ntice>>(score, nticeTuple.getDruhy());
    }

    /**
     * Provede flooding ve stavu a vrátí potenciální tahy pro minimax
     * 
     * @param stav
     */
    private ArrayList<Tah> najdiHratelnaPole(int[][] stav) {
        ArrayList<Tah> ret = new ArrayList<Tah>();

        for (int i = 0; i < ROZMERX; i++) {
            for (int j = 0; j < ROZMERY; j++) {
                ret.addAll(udelejZaplavu(stav, i, j));
            }
        }
        return ret;
    }

    /**
     * Najde pole, ktera kriticky hrozi koncem,
     * pokud nebudou pouzita (nezablokovana 3, 4)
     * 
     * @return
     */
    public ArrayList<Tah> najdiNutnaPole(ArrayList<Ntice> ntice, int cisloHrajiciho) {
        ArrayList<Tah> ret = new ArrayList<Tah>();
        // hledam ctverice (o jedno min, nez je vitezstvi)
        for (Ntice jednaNtice : ntice) {
            if (jednaNtice.getVelikost() != hraciPlocha.getPocetNaVitezstvi() - 1)
                continue;
            if (jednaNtice.getHrac() == cisloHrajiciho) {
                // hrac vyhral a staci jen jedno pole
                ret = new ArrayList<Tah>();
                ret.add(jednaNtice.getTahyKBlokaci().get(0));
                return ret;
            }

            ret.addAll(jednaNtice.getTahyKBlokaci());
        }
        if (!ret.isEmpty())
            return ret;

        // hledam s jednou dirou do petky trojice(o dve min, nez je vitezstvi)
        for (Ntice jednaNtice : ntice) {
            if (jednaNtice.getVelikost() != hraciPlocha.getPocetNaVitezstvi() - 2)
                continue;
            Tah dira = najdiDiru(jednaNtice);
            if (dira == null) //Neni dira
                continue;
            if (jednaNtice.getHrac() == cisloHrajiciho) {
                // hrac vyhral
                ret = new ArrayList<Tah>();
                ret.add(dira);
                return ret;
            }
            ret.add(dira);
        }
        if (!ret.isEmpty())
            return ret;

        // hledam nezablokovane trojice(o dve min, nez je vitezstvi)
        for (Ntice jednaNtice : ntice) {
            if (jednaNtice.getVelikost() == hraciPlocha.getPocetNaVitezstvi() - 2
                    && jednaNtice.getTahyKBlokaci().size() > 1) {
                if (jednaNtice.getHrac() == cisloHrajiciho) {
                    // hrac skoro vyhral a staci prozkoumat, ktery z rozsireni na 4 je lepsi
                    ret = new ArrayList<Tah>();
                    ret.addAll(jednaNtice.getTahyKBlokaci());
                    return ret;
                }
                ret.addAll(jednaNtice.getTahyKBlokaci());
            }
        }

        return ret;
    }

    /**
     * Hleda, jestli neblokovane pole neni dirou
     * @param jednaNtice
     * @return
     */
    private Tah najdiDiru(Ntice jednaNtice) {
        for (Tah tah : jednaNtice.getTahyKBlokaci()) {
            if(jePoleMoje(stavHraciPlochy,tah.getX()+jednaNtice.getDeltaX(),tah.getY()+jednaNtice.getDeltaY(),jednaNtice.getHrac())
            && jePoleMoje(stavHraciPlochy,tah.getX()-jednaNtice.getDeltaX(),tah.getY()-jednaNtice.getDeltaY(),jednaNtice.getHrac()))
            return tah;
        }
        return null;
    }

    /**
     * Provede "zaplavu" v konretnim bode do okoli maximalni hloubky. Dal nema smysl
     * hledat
     * 
     * @param stav
     * @param i
     * @param j
     * @return
     */
    private Collection<Tah> udelejZaplavu(int[][] stav, int i, int j) {
        ArrayList<Tah> ret = new ArrayList<Tah>();
        if (stav[i][j] <= PRAZDNE_POLE) {
            return ret;
        }
        for (int x = Math.max(-MAXIMALNI_HLOUBKA + i, 0); x <= Math.min(ROZMERX - 1, i + MAXIMALNI_HLOUBKA); x++) {
            for (int y = Math.max(-MAXIMALNI_HLOUBKA + j, 0); y <= Math.min(ROZMERY - 1, j + MAXIMALNI_HLOUBKA); y++) {
                // pokud tam je hrano, beru dalsi
                if (stav[x][y] != PRAZDNE_POLE) {
                    continue;
                }
                // mozny tah si zaznamenam
                stav[x][y] = POTENCIALNE_HRATELNE_POLE;
                ret.add(new Tah(x, y, null));
            }
        }
        return ret;
    }

    /**
     * Spocita cetnosti dvojic, trojic... v danem stavu plochy
     *
     * @param stav aktualni stav hry
     * @return cetnosti a ntice
     */
    public Dvojice<int[][][], ArrayList<Ntice>> spoctiCetnosti() {
        int[][][] stavCetnosti = new int[POCET_HRACU][hraciPlocha.getPocetNaVitezstvi()][2/*
                                                                                           * 0 neblokovano, 1 blokovano
                                                                                           * z jedne strany
                                                                                           */];
        ArrayList<Ntice> ntice = najdiNtice();
        for (Ntice jednaNtice : ntice) {
            if (jednaNtice.getVelikost() >= hraciPlocha.getPocetNaVitezstvi()) {
                // Vitezna sekvence uz nepotrebuje blokaci. Tak neni zablokovana
                stavCetnosti[jednaNtice.getHrac()][hraciPlocha.getPocetNaVitezstvi() - 1][0]++;
            } else {
                stavCetnosti[jednaNtice.getHrac()][jednaNtice.getVelikost() - 1][2
                        - jednaNtice.getTahyKBlokaci().size()]++;
            }
        }
        return new Dvojice<int[][][], ArrayList<Ntice>>(stavCetnosti, ntice);
    }

    /**
     * Najde vsechny n-tice v hracim planu, ktere maji delku aspon 2
     * 
     * @return
     */
    private ArrayList<Ntice> najdiNtice() {
        ArrayList<Ntice> ntice = new ArrayList<Ntice>();
        boolean jdeOFinalniStav = najdiNticeVeSmeru(ntice, 1, 0)
                || najdiNticeVeSmeru(ntice, 1, 1)
                || najdiNticeVeSmeru(ntice, 0, 1)
                || najdiNticeVeSmeru(ntice, -1, 1);
        return ntice;
    }

    /**
     * Najde n-tice - dvojice, trojice... a zapamatuje si statistiku k nim
     *
     * @param seznamNtic aktualni seznam n-tic
     * @param deltaX     vektor hledani v X
     * @param deltaY     vektor hledani v Y
     * @return nasel jsem vyherni sekvenci?
     */
    private boolean najdiNticeVeSmeru(ArrayList<Ntice> seznamNtic, int deltaX, int deltaY) {
        // Udelam si kopii stavu, abych ho nemenil tomu, kdo mi ho predal
        boolean[][] zpracovano = new boolean[ROZMERX][ROZMERY];

        for (int x = 0; x < ROZMERX; x++) {
            for (int y = 0; y < ROZMERY; y++) {
                int hrac = this.stavHraciPlochy[x][y];
                if (zpracovano[x][y] || hrac <= PRAZDNE_POLE) {
                    continue;
                }
                Ntice ntice = new Ntice(hrac, new Tah(x, y, null), deltaX, deltaY);
                if (!jeBlokovanePole(stavHraciPlochy, x - deltaX, y - deltaY)) {
                    ntice.getTahyKBlokaci().add(new Tah(x - deltaX, y - deltaY, null));
                }
                zpracovano[x][y] = true;
                int count = 1; // Aktuální symbol - uz je jeden

                // Kontrola v jednom směru
                for (int i = 1; true; i++) {
                    int newX = x + i * deltaX;
                    int newY = y + i * deltaY;

                    if (newX >= 0 && newX < ROZMERX && newY >= 0 && newY < ROZMERY
                            && stavHraciPlochy[newX][newY] == hrac) {
                        zpracovano[newX][newY] = true;
                        ntice.getKonec().setXY(newX, newY);
                        count++;
                    } else {
                        ntice.setVelikost(count);
                        if (!jeBlokovanePole(stavHraciPlochy, newX, newY)) {
                            ntice.getTahyKBlokaci().add(new Tah(newX, newY, null));
                        }

                        // Hlidat cetnost - spojenim dvou celku muze vzniknout delsi cara nez
                        // POCET_NA_VITEZSTVI
                        if (count >= hraciPlocha.getPocetNaVitezstvi()) {
                            seznamNtic.add(ntice);
                            return true; // Ostatni mne stejne nezajimaji, kdyz uz je takto uz skoncena hra
                        } else if (count > 1 && !ntice.getTahyKBlokaci().isEmpty()) { // Pokud neni zablokovano, ulozim
                                                                                      // si informaci delce a blokovani
                            seznamNtic.add(ntice);
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Zjisti, zda je dane pole blokujici pro Ntici
     * 
     * @param stav
     * @param x
     * @param y
     * @return
     */
    private static boolean jeBlokovanePole(int[][] stav, int x, int y) {
        return x < 0 || y < 0 || x >= ROZMERX || y >= ROZMERY || stav[x][y] > PRAZDNE_POLE;
    }
    /**
     * Zjisti, zda je dane pole daneho hrace
     * 
     * @param stav
     * @param x
     * @param y
     * @return
     */
    private static boolean jePoleMoje(int[][] stav, int x, int y, int mojeId) {
        return x >= 0 && y >= 0 && x < ROZMERX && y < ROZMERY && stav[x][y] == mojeId;
    }
}
