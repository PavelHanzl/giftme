package cz.pavelhanzl.giftme;

public class AddedUser {
    private String name;
    private String email;


    public AddedUser() {
        //prázdný konstruktor potřebný pro firebase firestore - NEMAZAT
    }

    public AddedUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
