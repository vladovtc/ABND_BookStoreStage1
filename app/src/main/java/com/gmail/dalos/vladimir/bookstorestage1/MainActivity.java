package com.gmail.dalos.vladimir.bookstorestage1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dalos.vladimir.bookstorestage1.data.BookContract.BookEntry;
import com.gmail.dalos.vladimir.bookstorestage1.data.BookDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_display)
    TextView textView;
    private BookDbHelper mDbHelper;

    @OnClick(R.id.fab)
    public void insertDummyData(View view) {

        insertBooks();
        queryData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Toast toast = Toast.makeText(this, "Insert dummy data --------->", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        mDbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        queryData();
    }

    private void queryData() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        try {

            textView.setText("This table contains " + cursor.getCount() + " books.\n\n");
            textView.append(BookEntry.COLUMN_BOOK_NAME + " - " +
                    BookEntry.COLUMN_ID + " - " +
                    BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER + " - " +
                    BookEntry.COLUMN_BOOK_PHONE + "\n");

            int idColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PHONE);

            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                textView.append(("\n"
                        + currentID + " - "
                        + currentName + " - "
                        + currentPrice + " - "
                        + currentQuantity + " - "
                        + currentSupplier + " - "
                        + currentPhone));
            }
        } finally {
            cursor.close();
        }
    }

    private void insertBooks() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "The Definitive Book of Body Language");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 99);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 2);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Book store");
        values.put(BookEntry.COLUMN_BOOK_PHONE, 1234);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, getString(R.string.row_not_saved_error_message), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.row_saved_successfully_message), Toast.LENGTH_SHORT).show();
        }

        Log.d("udacity", String.valueOf(newRowId));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
}
