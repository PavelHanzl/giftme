package cz.pavelhanzl.giftme.wishlist;

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

    public boolean isBooked() {
        if(bookedBy != null){
            return true;
        }
        return false;
    }
}
