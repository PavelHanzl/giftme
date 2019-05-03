package cz.pavelhanzl.giftme.stats;

import com.github.mikephil.charting.formatter.DefaultValueFormatter;

/**
 * Tuto třídu využívá fragment_stats pro korektní zobrazování čísel v koláčových grafech, kdy nebylo
 * žádoucí zobrazovat celá čísla i s desetinými místy. Např. "8,00" místo prostých "8".
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class DecimalFormater extends DefaultValueFormatter {
    private int digits;

     /**
     * Formátuje decimální část čísla a vrací číslo s tolika místy za desetinnou čátkou, kolik mu bylo předáno v parametru digits
     * @param digits
     */
    public DecimalFormater(int digits) {
        super(digits);
        this.digits = digits;
    }

    @Override
    public int getDecimalDigits() {
        return digits;
    }
}