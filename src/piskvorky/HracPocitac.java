/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piskvorky;

import static piskvorky.HraciPlocha.ROZMERX;
import static piskvorky.HraciPlocha.ROZMERY;

import java.util.ArrayList;

import static piskvorky.HraciPlocha.PRAZDNE_POLE;
import static piskvorky.HraciPlocha.POCET_HRACU;

/**
 * Trida reprezentujici hrace typu Pocitac
 * 
 * @author Tom
 */
public class HracPocitac extends Hrac {

    /**
     * Aktualni hraci plocha
     */
    private HraciPlocha hraciPlocha;
    /**
     * Maximalni hloubka rekurze
     */
    public final static int MAXIMALNI_HLOUBKA = 3;

    public HracPocitac(HraciPlocha hraciPlocha, String jmeno, int cislo, String symbol) {
        super(jmeno, cislo, symbol);
        this.hraciPlocha = hraciPlocha;
    }

    public Tah ziskatTah() {
        Tah nejlepsiTah = new Tah(0, 0, this);
        HracPocitacStav stav = new HracPocitacStav(hraciPlocha);
        // Jeste se netahlo a nebo je plna plocha.
        // Na plnou plochu by se nemelo volat toto, takze jde o prvni tah
        if (stav.getPotencionalniTahy().size() < 1) {
            // Tahni do prostred
            return new Tah(ROZMERX / 2, ROZMERY / 2, this);
        }
        findMax(stav, 0, getCislo(), nejlepsiTah, Integer.MAX_VALUE);
        return nejlepsiTah;
    }

    private int findMax(HracPocitacStav stav, int depth, int hrac, Tah nejlepsiTahNaPatre,
            int aktualniNejlepsiNad) {

        nejlepsiTahNaPatre.setXY(-1, -1);

        // Neni kam hrat
        if (stav.potencionalniTahy.isEmpty()) {
            return 0;
        }

        Dvojice<Integer, ArrayList<Ntice>> scoreTuple = stav.vyhodnotitPodleCetnosti(getCislo());
        int score = scoreTuple.getPrvni();

        // Nekdo vyhral, dal uz se nehleda
        if (score == Integer.MAX_VALUE || score == Integer.MIN_VALUE) {
            return score;
        }

        // zarazka rekurze
        if (depth > MAXIMALNI_HLOUBKA) {
            return score;
        }

        // Pokud jsem uz nekde ziskal nejlepsi mozne skore, tak uz nemusim prohledavat
        // dalsi vetve
        if (aktualniNejlepsiNad == Integer.MIN_VALUE) {
            return score;
        }

        ArrayList<Tah> mnozinaRelevantnichTahu = stav.najdiNutnaPole(scoreTuple.getDruhy(), hrac);
        // Pokud nejsou nutna pole, tak muzu tahnout kamkoliv
        if (mnozinaRelevantnichTahu.isEmpty())
            mnozinaRelevantnichTahu = stav.getPotencionalniTahy();
        int bestScore = Integer.MIN_VALUE;
        for (int i = stav.getPotencionalniTahy().size(); i-- > 0;) {
            Tah tah = stav.getPotencionalniTahy().get(i);
            // zkoumam jen relevantni tahy
            if (!mnozinaRelevantnichTahu.contains(tah))
                continue;
            stav.getPotencionalniTahy().remove(tah);
            // Nastavim docasne tah
            stav.getStavHraciPlochy()[tah.x][tah.y] = hrac;
            int dalsiHrac = (hrac + 1) % POCET_HRACU;
            Tah kVyhozeni = new Tah(0, 0, this);
            int minmax = findMin(stav, depth + 1, dalsiHrac, kVyhozeni, bestScore);

            // Aktualni tah je nejlepsi
            if (bestScore < minmax) {
                bestScore = minmax;
                nejlepsiTahNaPatre.setXY(tah);
            }

            // Vracim tah. Nastavim v hraci plose, ze tu nikdo nehral
            stav.getStavHraciPlochy()[tah.x][tah.y] = PRAZDNE_POLE;
            stav.getPotencionalniTahy().add(tah);

            // Pokud jde o lepsi skore, nez paralelni vetev a nebo jde o vyhru, dal
            // nezkoumam tuto vetev
            if (bestScore > aktualniNejlepsiNad || bestScore == Integer.MAX_VALUE) {
                return bestScore;
            }
        }
        //Nic se nenaslo? Vsechno vede k prohre?
        if(nejlepsiTahNaPatre.getX()<0)
        {
            //Vezmi jeden z blokujicicich (nutnych) tahu
            nejlepsiTahNaPatre.setXY(mnozinaRelevantnichTahu.get(0));
        }
        return bestScore;
    }

    private int findMin(HracPocitacStav stav, int depth, int hrac, Tah nejlepsiTahNaPatre,
            int aktualniNejlepsiNad) {

        // Neni kam hrat
        if (stav.getPotencionalniTahy().isEmpty()) {
            return 0;
        }

        Dvojice<Integer, ArrayList<Ntice>> scoreTuple = stav.vyhodnotitPodleCetnosti(getCislo());
        int score = scoreTuple.getPrvni();

        // Nekdo vyhral, dal uz se nehleda
        if (score == Integer.MAX_VALUE || score == Integer.MIN_VALUE) {
            return score;
        }

        // zarazka rekurze
        if (depth > MAXIMALNI_HLOUBKA) {
            return score;
        }

        // Pokud jsem uz nekde ziskal nejlepsi mozne skore, tak uz nemusim prohledavat
        // dalsi vetve
        if (aktualniNejlepsiNad == Integer.MAX_VALUE) {
            return score;
        }
        ArrayList<Tah> mnozinaRelevantnichTahu = stav.najdiNutnaPole(scoreTuple.getDruhy(), hrac);
        // Pokud nejsou nutna pole, tak muzu tahnout kamkoliv
        if (mnozinaRelevantnichTahu.isEmpty())
            mnozinaRelevantnichTahu = stav.getPotencionalniTahy();

        int bestScore = Integer.MAX_VALUE;
        for (int i = stav.getPotencionalniTahy().size(); i-- > 0;) {
            Tah tah = stav.getPotencionalniTahy().get(i);
            // zkoumam jen relevantni tahy
            if (!mnozinaRelevantnichTahu.contains(tah))
                continue;
            stav.getPotencionalniTahy().remove(tah);
            // Nastavim docasne tah
            stav.getStavHraciPlochy()[tah.x][tah.y] = hrac;
            int dalsiHrac = (hrac + 1) % POCET_HRACU;
            int minmax = dalsiHrac == getCislo()
                    ? findMax(stav, depth + 1, dalsiHrac, nejlepsiTahNaPatre, bestScore)
                    // V pripade, ze hraje vice hracu najednou, pak muze volat Min znovu Min
                    : findMin(stav, depth + 1, dalsiHrac, nejlepsiTahNaPatre, bestScore);

            // Aktualni tah je nejlepsi
            if (bestScore > minmax) {
                bestScore = minmax;
                nejlepsiTahNaPatre.setXY(tah);
            }

            // Vracim tah. Nastavim v hraci plose, ze tu nikdo nehral
            stav.getStavHraciPlochy()[tah.x][tah.y] = PRAZDNE_POLE;
            stav.getPotencionalniTahy().add(tah);

            // Pokud jde o horsi skore, nez paralelni vetev a nebo jde o prohru, dal
            // nezkoumam tuto vetev
            if (bestScore < aktualniNejlepsiNad || bestScore == Integer.MIN_VALUE) {
                return bestScore;
            }

        }
        return bestScore;
    }

}
