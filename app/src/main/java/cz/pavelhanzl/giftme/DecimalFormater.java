package cz.pavelhanzl.giftme;

import com.github.mikephil.charting.formatter.DefaultValueFormatter;

public class DecimalFormater extends DefaultValueFormatter {
    private int digits;

    //Formátuje decimální část čísla a vrací číslo s tolika místy za desetinnou čátkou, kolik mu bylo předáno v parametru digits
    public DecimalFormater(int digits) {
        super(digits);
        this.digits = digits;
    }

    @Override
    public int getDecimalDigits() {
        return digits;
    }
}