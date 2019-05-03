package cz.pavelhanzl.giftme.giftlist.persons_giftlist;
/**
 * Model pro dárky zobrazované v Menu->Giftlists->Osoba a Menu->Giftlists->Osoba->archiv.
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}
