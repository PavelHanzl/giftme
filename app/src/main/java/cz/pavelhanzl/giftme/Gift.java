package cz.pavelhanzl.giftme;

public class Gift {
    private String name;
    private int price;
    private boolean archived;
    private boolean bought;

    public Gift(String name, int price, boolean archived, boolean bought) {
        this.name = name;
        this.price = price;
        this.archived = archived;
        this.bought = bought;
    }

    public Gift() {
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isBought() {
        return bought;
    }
}
