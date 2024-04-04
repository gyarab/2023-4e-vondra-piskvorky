/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piskvorky;

import java.util.Objects;

/**
 *
 * @author Tom
 */
public class Tah {
    int x;
    int y;
    Hrac hrac;

    public Tah(int x, int y, Hrac hrac) {
        this.x = x;
        this.y = y;
        this.hrac = hrac;
    }

    public int getX() {
        return x;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setXY(Tah tah) {
        this.x = tah.x;
        this.y = tah.y;
    }

    public int getY() {
        return y;
    }

    public Hrac getHrac() {
        return hrac;
    }

    public void setHrac(Hrac hrac) {
        this.hrac = hrac;
    }

    @Override
    public boolean equals(Object o) {
        // zkontroluji, jestli to neni stejny objekt
        if (this == o)
            return true;
        // zkontroluji, jestli nejde o null
        if (o == null)
            return false;
        // udelam typovou kontrolu
        if (getClass() != o.getClass())
            return false;
        Tah tah = (Tah) o;
        // field comparison
        return Objects.equals(x, tah.x)
                && Objects.equals(y, tah.y);
    }
}
