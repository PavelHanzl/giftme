package cz.pavelhanzl.giftme.wishlist;
/**
 * Model pro gifttipy zobrazované v Menu->Friends->Osoba->Tab1 a Tab2.
 */
public class GiftTip {
    private String name;
    private String tipBy;
    private String bookedBy;

    public GiftTip() {
    }

    public GiftTip(String name, String tipBy, String bookedBy) {
        this.name = name;
        this.tipBy = tipBy;
        this.bookedBy = bookedBy;
    }

    public String getName() {
        return name;
    }

    public String getTipBy() {
        return tipBy;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTipBy(String tipBy) {
        this.tipBy = tipBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    /**
     * Pokud je gifttip někým rezervován, tak vrátí true, jinak vrací false.
     * @return
     */
    public boolean isBooked() {
        if(bookedBy != null){
            return true;
        }
        return false;
    }
}
