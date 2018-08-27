package com.gmail.dalos.vladimir.bookstorestage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.dalos.vladimir.bookstorestage2.data.BookContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    @BindView(R.id.tv_empty_view)
    View emptyView;
    BooksCursorAdapter mCursorAdapter;

    @OnClick(R.id.fab_main)
    public void clickFabMain(View view) {

        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        ListView listView = findViewById(R.id.list_view);
        listView.setEmptyView(emptyView);
        mCursorAdapter = new BooksCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    public void buttonSale(int bookId, int bookQuantity) {
        bookQuantity -= 1;
        if (bookQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
            int rowsAffected = getContentResolver().update(updateUri, values,
                    null, null);
            Log.d("clicked", "Message" + rowsAffected + bookId + bookQuantity);
            Toast.makeText(this, "Quantity is changed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing to sell", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertBooks() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "The Definitive Book of Body Language");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 99);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 2);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Book store");
        values.put(BookEntry.COLUMN_BOOK_PHONE, 1234);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI,
                null, null);
        Toast.makeText(this, rowsDeleted + " books are deleted", Toast.LENGTH_SHORT).show();
        Log.v("MainActivity", rowsDeleted + " rows deleted from book database");
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
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_books:
                insertBooks();
                return true;
            case R.id.action_delete_all:
                deleteAllDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllBooks();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_PHONE};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
