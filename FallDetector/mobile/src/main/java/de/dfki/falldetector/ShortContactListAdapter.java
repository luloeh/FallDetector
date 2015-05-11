package de.dfki.falldetector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dfki.falldetectorcommons.ShortContact;

/**
 * This class contains the list of selected contacts and handles their display
 */
public class ShortContactListAdapter extends BaseAdapter implements ListAdapter {

    private List<ShortContact> contacts;
    private Context context;
    private static final String TAG = "adapter";

    public ShortContactListAdapter(List<ShortContact> contacts, Context context){
        this.contacts = contacts;
        this.context = context;
    }

    public void addContact(ShortContact contact){
        if(!contacts.contains(contact)) {
            contacts.add(contact);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        //inflate list item layout
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }
        ShortContact contact = contacts.get(i);
        if(contact == null){
            Log.e(TAG,"got null element in contacts");
            return view;
        }
        //try to get image for contact
        ImageView pic = (ImageView) view.findViewById(R.id.list_item_image);
        String imgUrl = contact.getPicture();
        if(imgUrl == null || imgUrl.isEmpty()){
            //load default contact image
            Drawable contactImage = context.getResources().getDrawable(R.drawable.ic_contact_picture);
            pic.setImageDrawable(contactImage);
        }
        else{
            pic.setImageURI(Uri.parse(imgUrl));
        }
        //add contact data to view
        TextView name = (TextView)view.findViewById(R.id.list_item_name);
        name.setText(contact.getName());

        TextView number = (TextView)view.findViewById(R.id.list_item_number);
        number.setText(contact.getNumber());

        ImageButton addButton = (ImageButton)view.findViewById(R.id.list_item_delete);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts.remove(i);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
