package de.dfki.falldetector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dfki.falldetectorcommons.ShortContact;

/**
 * This activity allows the user to select emergency contacts from their phone contacts
 *
 * The emergency contacts are then saved as a set in shared preferences
 */
public class ContactSelector extends ActionBarActivity {

    private ShortContactListAdapter adapter;
    private SharedPreferences prefs;
    private static final String TAG = "ContactSelector";
    final List<ShortContact> contacts = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "selector onCreate called");
        //get the shared preferences we load from and save contacts to
        prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_MULTI_PROCESS);
        //load saved contacts
        Set<String> loadedContacts = prefs.getStringSet(getString(R.string.contacts_key), null);
        setContentView(R.layout.activity_main);
        if(loadedContacts != null) {
            for (String s : loadedContacts) {
                ShortContact sc = ShortContact.fromStorageString(s);
                if (sc != null) {
                    contacts.add(sc);
                }
            }
        }
        adapter = new ShortContactListAdapter(contacts, this);
        //display contacts in a ListView backed by a ShortContactListAdapter
        ListView list = (ListView) findViewById(R.id.contact_list);
        list.setAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * The method bound to the add-contact button
     * @param view
     */
    public void addContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(pickContactIntent, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            //didn't get contact
            return;
        }
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        //get contact name
        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
        String name = cursor.getString(nameIndex);
        //get contact number
        int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String phoneNumber = cursor.getString(phoneNumberIndex);
        //get contact image
        int photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.PHOTO_THUMBNAIL_URI);
        String photoURI = cursor.getString(photoIndex);
        //create & add new contact
        ShortContact contact = new ShortContact(name, phoneNumber, photoURI);
        cursor.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        super.onStop();
        //save selected contacts to shared preferences
        SharedPreferences.Editor ed = prefs.edit();
        Set<String> storageStrings = new HashSet<>();
        for (ShortContact c : contacts) {
            storageStrings.add(c.toStorageString());
        }
        ed.putStringSet(getString(R.string.contacts_key), storageStrings);
        ed.apply();
    }
}
