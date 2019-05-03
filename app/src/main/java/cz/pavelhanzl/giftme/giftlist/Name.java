package cz.pavelhanzl.giftme.giftlist;

/**
 * Model pro jednotlivé osoby přidané do Menu->Giftlists.
 *
 * @author  Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
public class Name {
    private String name;
    private int budget;


    public Name() {
        //prázdný konstruktor potřebný pro firebase firestore - NEMAZAT
    }

    public Name(String name, int budget) {
        this.name = name;
        this.budget = budget;
    }

    public String getName() {
        return name;
    }

    public int getBudget() {
        return budget;
    }
}
