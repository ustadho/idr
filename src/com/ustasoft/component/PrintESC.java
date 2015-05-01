/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ustasoft.component;

/**
 *
 * @author cak-ust
 */
public class PrintESC {

    public String resetPrn() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 64);
        return str;
    }

    public String copyString(String sTeks, int panjang) {
        String newString = "";
        for (int y = 1; y <= panjang; y++) {
            newString = sTeks + newString;
        }
        return newString;
    }
    
    public String draft() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 48);
        return str;
    }

    public String LQ() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 49);
        return str;
    }

    public String bold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 69);
        return str;
    }

    public String cancelBold() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 70);
        return str;
    }

    public String italic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 52);
        return str;
    }

    public String cancelItalic() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 53);
        return str;
    }

    public String underLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 49);
        return str;
    }

    public String cancelUnderLine() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 45) + String.valueOf((char) 48);
        return str;
    }

    public String cpi10() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 80);
        return str;
    }

    public String cpi12() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 77);
        return str;
    }

    public String cpi15() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 103);
        return str;
    }

    public String condenced() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 15);
        return str;
    }

    public String cancelCondenced() {
        String str;
        str = String.valueOf((char) 18);
        return str;
    }

    public String loadFront() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 25) + String.valueOf((char) 70);
        return str;
    }

    public String DoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 71);
        return str;
    }

    public String CancelDoubleStrike() {
        String str;
        str = String.valueOf((char) 27) + String.valueOf((char) 72);
        return str;

    }

    public String rataKanan(String sTeks, int panjang) {
        String newText;

        newText = space(panjang - sTeks.length()) + sTeks;

        return newText;
    }

    public String padString(String sTeks, int panjang) {
        String newText;
        String jmSpace = "";
        if (sTeks.trim().trim().length() > panjang) {
            newText = sTeks.trim().substring(0, panjang);
        } else {
            newText = sTeks.trim();
        }


        for (int i = 0; i < (panjang - sTeks.trim().length()); i++) {
            newText = newText + " ";
        }

        return newText;
    }

    public String space(int iSpc) {
        String s = "";
        for (int i = 1; i <= iSpc; i++) {
            s = s + " ";
        }
        return s;
    }

    public String tengah(String sStr, int panjang) {
        String s = "";
        int iTengah = (panjang - sStr.length()) / 2;
        s = s + space(iTengah) + sStr;
        return s;
    }
}
