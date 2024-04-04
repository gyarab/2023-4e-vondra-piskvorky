package piskvorky;

import javafx.concurrent.Task;

public class PocitaciVlakno extends Task<Tah> {
    private HracPocitac _pocitac;
    public PocitaciVlakno(HracPocitac pocitac) {
        _pocitac = pocitac;
    }

    @Override
    public Tah call() {
        updateMessage("Hledám tah");
        Tah tah = _pocitac.ziskatTah();
        updateMessage("Tah nalezen");
        return tah;
    }  
}
