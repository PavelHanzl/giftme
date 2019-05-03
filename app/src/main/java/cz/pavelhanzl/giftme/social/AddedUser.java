package cz.pavelhanzl.giftme.social;

/**
 * Model pro uživatele zobrazované v Menu->Friends, které si přidáte do přátel.
 *
 * @author Pavel Hanzl
 * @version 1.04
 * @since   03-05-2019
 */
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
