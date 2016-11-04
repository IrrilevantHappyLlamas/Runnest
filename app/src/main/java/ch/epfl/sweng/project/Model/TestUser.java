package ch.epfl.sweng.project.Model;

/**
 * Class that implements the <code>User</code> interface, used when we need to test the app without
 * an authenticated user in our app
 */
public class TestUser implements User {

    private String id = null;
    private String email = null;
    private String familyName = null;
    private String name = null;
    private String photoUrl = null;
    private String firebaseID = null;

    public TestUser() {
        id = "Test User";
        email = "Test User";
        familyName = "Test User";
        name = "Test User";
        photoUrl = "";
        firebaseID = "6VauzC82b6YoNfRSo2ft4WFqoCu1";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public String getFirebaseId() {
        return firebaseID;
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void logoutStatus() {

    }

    @Override
    public void loginStatus() {

    }
}
