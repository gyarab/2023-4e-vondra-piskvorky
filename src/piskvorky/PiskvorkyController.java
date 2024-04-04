        /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piskvorky;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Tom
 */
public class PiskvorkyController implements Initializable {

    HraciPlocha hraciPlocha;
    ArrayList<Hrac> seznamHracu = new ArrayList<Hrac>();
    private PocitaciVlakno vlakno;
    private boolean cekaSeNaCloveka = false;
    private boolean dosloKChybe = false;

    @FXML
    private Label label;
    @FXML
    private GridPane grid;
    @FXML
    private TextField informacniOkno;
    @FXML
    private RadioButton pocitac1;
    @FXML
    private RadioButton pocitac2;
    @FXML
    private RadioButton p4;
    @FXML
    private RadioButton p5;
    @FXML
    private RadioButton p6;
    @FXML
    private TextField jmenoHrac1;
    @FXML
    private TextField jmenoHrac2;
    @FXML
    private TextField znakHrace1;
    @FXML
    private TextField znakHrace2;
    @FXML
    private Button novaHra;
    @FXML
    private Button konecHry;

    @FXML
    private void udelejTah(ActionEvent event) {
        if (!cekaSeNaCloveka || dosloKChybe || hraciPlocha.jeKonec()) {
            // Neni mozne hrat
            return;
        }

        Button tlacitko = (Button) event.getTarget();
        if (tlacitko.getText() != "") {
            // Uz zde bylo tazeno
            return;
        }
        Tah tah = ziskatTahZTlacitka(tlacitko);
        zahratTah(tah);
        cekaSeNaCloveka = false;
        udelejDalsiTah();
    }

    @FXML
    private void novaHra(ActionEvent event) {
        spustitHru();
    }

    @FXML
    private void nastavTypHrace1(ActionEvent event) {
        if (hraciPlocha == null) {
            return;
        }
        Hrac jedna = vytvoreniHracePodleTypu(pocitac1, seznamHracu.get(0).getJmeno(), seznamHracu.get(0).getCislo(),
                seznamHracu.get(0).getSymbol());
        seznamHracu.remove(0);
        seznamHracu.add(0, jedna);
    }

    @FXML
    private void nastavTypHrace2(ActionEvent event) {
        if (hraciPlocha == null) {
            return;
        }
        Hrac dva = vytvoreniHracePodleTypu(pocitac2, seznamHracu.get(1).getJmeno(), seznamHracu.get(1).getCislo(),
                seznamHracu.get(1).getSymbol());
        seznamHracu.remove(1);
        seznamHracu.add(1, dva);
    }

    @FXML
    private void konecHry(ActionEvent event) {
        umozneniEditace(true);
        zobrazHlasku("Hra byla ukončena");
        hraciPlocha.setKonec(true);
    }

    /**
     * Provede dalsi tah
     */
    private void udelejDalsiTah() {
        // Konec hry, nic dal nedelam
        if (hraciPlocha.jeKonec() || dosloKChybe)
            return;

        Hrac hracNaTahu = seznamHracu.get(hraciPlocha.getHracNaTahu());
        if (hracNaTahu instanceof HracClovek) {
            cekaSeNaCloveka = true;
            // pokud jde o hrace Cloveka koncim a prenecham rizeni grafice
            zobrazHlasku("Nyní hraje " + hracNaTahu.getJmeno());
            return;
        }
        if (hracNaTahu instanceof HracPocitac) {
            zobrazHlasku(hracNaTahu.getJmeno() + " hledá nejlepší tah");
            vlakno = new PocitaciVlakno((HracPocitac) hracNaTahu);

            vlakno.setOnSucceeded((succeededEvent) -> {
                Tah tah = vlakno.getValue();
                zahratTah(tah);
                udelejDalsiTah();
            });

            vlakno.setOnFailed(e -> {
                vlakno.getException().printStackTrace(); // or log it with a proper logging framework
                zobrazHlasku(vlakno.getException().getMessage());
            });

            new Thread(vlakno).start();
            return;
        }
        udelejDalsiTah();
    }

    private Tah ziskatTahZTlacitka(Button tlacitko) {
        String id = tlacitko.getId();
        String[] casti = id.split("_");
        int x = Integer.parseInt(casti[1]);
        int y = Integer.parseInt(casti[2]);

        Tah tah = new Tah(x, y, ziskejHraceNaTahu());
        return tah;
    }

    private void spustitHru() {
        List<String> hlasky = validaceSpusteniHry();
        if (!hlasky.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String hlaska : hlasky) {
                message.append(hlaska).append("\n");
            }
            JOptionPane.showMessageDialog(null, message, "Seznam hlášek", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        umozneniEditace(false);
        hraciPlocha = new HraciPlocha(ziskejPocetNaVitezstvi());
        for (int x = 0; x < HraciPlocha.ROZMERX; x++)
            for (int y = 0; y < HraciPlocha.ROZMERY; y++)
                nastavSymbolNaPole(x, y, "","");
        cekaSeNaCloveka = true;
        dosloKChybe = false;
        seznamHracu.clear();
        // seznamHracu.add(new HracClovek("Franta", 0, "X"));
        seznamHracu.add(vytvorHrace1());
        seznamHracu.add(vytvorHrace2());
        udelejDalsiTah();
    }

    private void umozneniEditace(boolean umozneni) {
        jmenoHrac1.setDisable(!umozneni);
        jmenoHrac2.setDisable(!umozneni);
        znakHrace1.setDisable(!umozneni);
        znakHrace2.setDisable(!umozneni);
        p4.setDisable(!umozneni);
        p5.setDisable(!umozneni);
        p6.setDisable(!umozneni);
        konecHry.setDisable(umozneni);
        novaHra.setDisable(!umozneni);
    }

    private int ziskejPocetNaVitezstvi() {
        if (p4.isSelected()) {
            return 4;
        }
        if (p5.isSelected()) {
            return 5;
        }
        if (p6.isSelected()) {
            return 6;
        }
        return 5;
    }

    public List<String> validaceSpusteniHry() {
        ArrayList<String> validacniHlasky = new ArrayList<String>();
        if (jmenoHrac1.getText().equals("")) {
            validacniHlasky.add("Není nastaveno jméno hráče 1");
        }
        if (jmenoHrac2.getText().equals("")) {
            validacniHlasky.add("Není nastaveno jméno hráče 2");
        }
        if (jmenoHrac2.getText().equals(jmenoHrac1.getText())) {
            validacniHlasky.add("Jména hráčů musí být rozdílná");
        }
        if (znakHrace1.getText().length() != 1) {
            validacniHlasky.add("Symbol hráče 1 musí obsahovat právě jeden znak");
        }
        if (znakHrace2.getText().length() != 1) {
            validacniHlasky.add("Symbol hráče 2 musí obsahovat právě jeden znak");
        }
        if (znakHrace1.getText().equals(znakHrace2.getText())) {
            validacniHlasky.add("Symboly nesmí být shodné");
        }
        return validacniHlasky;
    }

    private Hrac vytvorHrace2() {
        return vytvoreniHracePodleTypu(pocitac2, jmenoHrac2.getText(), 1, znakHrace2.getText(0, 1));
    }

    private Hrac vytvorHrace1() {
        return vytvoreniHracePodleTypu(pocitac1, jmenoHrac1.getText(), 0, znakHrace1.getText(0, 1));
    }

    /**
     * Zpracuje chybu, kterou vrati zahratTah
     * 
     * @param e
     */
    private void zpracujChybu(Exception e) {
        cekaSeNaCloveka = false;
        dosloKChybe = true;
        umozneniEditace(true);
        zobrazHlasku(e.getMessage());
    }

    private void zahratTah(Tah tah) {
        nastavSymbolNaPole(tah.x, tah.y, tah.hrac.getSymbol(), "hrac"+tah.hrac.getCislo());
        try {
            hraciPlocha.zahratTah(tah);
            zobrazHlasku("Hráč " + tah.hrac.getJmeno() + " zahrál tah [" + tah.x + "," + tah.y + "]");
            if (hraciPlocha.jeKonec())
                zobrazHlasku("Hráč " + tah.hrac.getJmeno() + " vyhrál");
            if (hraciPlocha.jeKonec()) {
                umozneniEditace(true);
            }
        } catch (Exception e) {
            zpracujChybu(e);
        }
    }

    private void nastavSymbolNaPole(int x, int y, String symbol, String cssClass) {
        Button tlacitko;
        String idTlacitka = "#pole_" + x + "_" + y;
        tlacitko = (Button) grid.lookup(idTlacitka);
        tlacitko.setText(symbol);
        tlacitko.getStyleClass().clear();
        tlacitko.getStyleClass().add("button");
        tlacitko.getStyleClass().add(cssClass);
    }

    private void zobrazHlasku(String hlaska) {
        informacniOkno.setText(hlaska);
    }

    private Hrac vytvoreniHracePodleTypu(RadioButton radio, String jmeno, int cislo, String symbol) {
        Hrac novy = radio.isSelected()
                ? new HracPocitac(hraciPlocha, jmeno, cislo, symbol)
                : new HracClovek(jmeno, cislo, symbol);
        return novy;
    }

    private Hrac ziskejHraceNaTahu() {
        return seznamHracu.get(hraciPlocha.getHracNaTahu());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
