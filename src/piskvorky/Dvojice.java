package piskvorky;

/**
 * Genericka trida pro reprezentaci dvojice
 */
public class Dvojice<K, V> {

    private K prvni;
    private V druhy;
  
    /**
     * Konstruktor dvojice
     * @param first
     * @param second
     */
    public Dvojice(K first, V second){
        this.prvni = first;
        this.druhy = second;
    }

    /**
     * Prvni
     * @return
     */
    public K getPrvni() {
        return prvni;
    }

    /**
     * Druhy
     * @return
     */
    public V getDruhy() {
        return druhy;
    }

}