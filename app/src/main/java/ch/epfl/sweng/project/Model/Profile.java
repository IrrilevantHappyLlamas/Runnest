package ch.epfl.sweng.project.Model;

/**
 * Profile class, store information about the user
 *
 * @author Riccardo Conti
 */
public class Profile {
    private String id;
    private String email;
    private String familyName;
    private String name;

    public Profile(String id, String email, String familyName, String name) {
        this.id = id;
        this.email = email;
        this.familyName = familyName;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getName() {
        return name;
    }
}
