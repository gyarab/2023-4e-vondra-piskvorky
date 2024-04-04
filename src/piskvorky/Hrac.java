
package piskvorky;

/**
 * Abstraktni trida jako predek vsech typu hracu
 * @author Tom
 */
public abstract class Hrac {
    private String jmeno;
    private int cislo;
    private String symbol;

    /**
     * Plny konstruktor abstraktniho hrace
     * @param jmeno
     * @param cislo
     * @param symbol
     */
    public Hrac(String jmeno, int cislo, String symbol) {
        this.jmeno = jmeno;
        this.cislo = cislo;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getCislo() {
        return cislo;
    }

    public String getJmeno() {
        return jmeno;
    }    

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public void setCislo(int cislo) {
        this.cislo = cislo;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
}
