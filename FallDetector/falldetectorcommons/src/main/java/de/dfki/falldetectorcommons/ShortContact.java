package de.dfki.falldetectorcommons;

import android.text.TextUtils;
import android.util.Log;

/**
 * This class encapsulates data of selected contacts,
 * Name and imageURI for display in the list and the mobile number
 * to send emergency SMS
 */
public class ShortContact {

    private static final String TAG = "ShortContact";

    /**
     * Create a new ShortContact
     * @param name Display Name of the contact
     * @param number Number of the contact, may not be null
     * @param imageURI Uri of contact image
     */
    public ShortContact(String name, String number, String imageURI) {
        if(number == null){
            throw new IllegalArgumentException("number may not be null");
        }
        this.name = name;
        this.number = number;
        this.picture = imageURI;
    }

    /**
     * Retrieves the number of a stored contact
     * @param storageString the string representation of a contact loaded from shared preferences
     * @return the saved mobile number or <code>null</code> if the data is incomplete
     */
    public static String parseNumber(String storageString){
        String[] members = TextUtils.split(storageString, "\\<\\$\\#\\$\\>");
        if (members.length != 3) {
            Log.e(TAG,"error splitting shortContact string, got only "+members.length+" values out of "+storageString);
            return null;
        }
        return members[2];
    }

    /**
     * converts this contact to a string that can be saved in a text file or similar
     * @return String representation of this contact
     */
    public String toStorageString() {
        String[] members = {name, number, picture};
        return TextUtils.join("<$#$>", members);
    }

    /**
     * Create a new contact from string representation
     * @param storageString a string representation obtained from {@link #toStorageString()}
     * @return
     */
    public static ShortContact fromStorageString(String storageString) {
        String[] members = TextUtils.split(storageString, "\\<\\$\\#\\$\\>");
        if (members.length != 3) {
            Log.e(TAG,"error splitting shortContact string, got only "+members.length+" values out of "+storageString);
            return null;
        }
        return new ShortContact(members[0], members[1], members[2]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortContact that = (ShortContact) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!number.equals(that.number)) return false;
        if (picture != null ? !picture.equals(that.picture) : that.picture != null) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + number.hashCode();
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        return result;
    }

    private String name;
    private String number;
    private String picture;

    public String getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
