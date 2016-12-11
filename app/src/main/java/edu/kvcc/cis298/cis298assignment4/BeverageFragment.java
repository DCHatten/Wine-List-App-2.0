package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;

/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageFragment extends Fragment {

    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "beverage_id";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_DATE = 0;

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;
    private Button mReportButton;
    private Button mContactButton;
    private String mContact;
    private String mEmail;

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflator to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });

        mReportButton = (Button)view.findViewById(R.id.beverage_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting a new intent to send the data
                Intent i = new Intent(Intent.ACTION_SENDTO);
                //Setting the data type to plain text
                i.setData(Uri.parse("mailto:" + mEmail));
                //Putting the beverage details into the intent, using the get
                //Beverage Details method
                i.putExtra(Intent.EXTRA_TEXT, getBeverageDetails());
                //Adding a subject from the contact string
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.beverage_details_subject));
                //Make sure the app prompts the user for which app they want to use
                i = Intent.createChooser(i, getString(R.string.beverage_report));
                //Start up the activity
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);


        mContactButton = (Button)view.findViewById(R.id.contact);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        //Checking to see if the contact string has changed, and if so, setting the button to the new value
        if (getString(R.string.contact) != "Select Contact") {
            mContactButton.setText(getString(R.string.contact));
        }

        //Check to see if a contacts app is available, and if not, disable the contact button
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactButton.setEnabled(false);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the result code is not OK, we won't do any work.
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //Check to see if there was a contact selected
        if (requestCode == REQUEST_CONTACT && data != null) {
            //Get the contactUri from the returned data
            Uri contactUri = data.getData();
            //Set a string array to hold contact names
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Set a string array to hold the contact emails
            String[] queryEmail = new String[] {
                    ContactsContract.CommonDataKinds.Email.DATA
            };
            //Define a cursor to pull the contact names queried
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            //Define a second cursor to pull the contact emails queried
            Cursor cur = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID, null, null);

            try {
                if (c.getCount() == 0 && cur.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                cur.moveToFirst();
                mContact = c.getString(0);
                mEmail = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                mContactButton.setText(mContact);
            } finally {
                c.close();
            }
        }
    }

    private String getBeverageDetails() {

        //Set the string for whether the crime is solved to null
        String isActive = null;

        //If the crime is solved, we will set string to the solved
        //string stored in strings.xml. Otherwise, the unsolved string.
        if (mBeverage.isActive()) {
            isActive = getString(R.string.beverage_is_active);
        } else {
            isActive = getString(R.string.beverage_not_active);
        }

        String price = "$" + Double.toString(mBeverage.getPrice());

        //Create the final report string using the above created strings
        //as the parameters for the crime_report string.
        String report = getString(R.string.beverage_details,
                mContact,
                mBeverage.getId(),
                mBeverage.getName(),
                mBeverage.getPack(),
                price,
                isActive);

        //Return the final built report string
        return report;
    }


}
