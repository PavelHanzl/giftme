package cz.pavelhanzl.giftme.giftlist;

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
