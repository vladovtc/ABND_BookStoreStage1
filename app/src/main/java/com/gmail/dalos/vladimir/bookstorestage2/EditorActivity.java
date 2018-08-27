package com.gmail.dalos.vladimir.bookstorestage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gmail.dalos.vladimir.bookstorestage2.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    @BindView(R.id.et_book_name)
    EditText bookName;
    @BindView(R.id.et_book_price)
    EditText bookPrice;
    @BindView(R.id.et_book_quantity)
    EditText bookQuantity;
    @BindView(R.id.et_supplier_name)
    EditText bookSupplierName;
    @BindView(R.id.et_supplier_phone)
    EditText bookSupplierPhone;
    private Uri mCurrentUri;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_a_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER,
                    null, this);
        }
    }

    private void saveBook() {

        String nameString = bookName.getText().toString().trim();
        String priceString = bookPrice.getText().toString().trim();
        String quantityString = bookQuantity.getText().toString().trim();
        String supplierNameString = bookSupplierName.getText().toString().trim();
        String supplierPhoneString = bookSupplierPhone.getText().toString().trim();

        if (mCurrentUri == null) {
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, "Requires a name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, "Requires price", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Requires quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierNameString)) {
                Toast.makeText(this, "Requires suppliers name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierPhoneString)) {
                Toast.makeText(this, "Requires a supplierPhone number", Toast.LENGTH_SHORT).show();
            }

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierNameString);
            values.put(BookEntry.COLUMN_BOOK_PHONE, supplierPhoneString);

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error saving book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER, supplierNameString);
            values.put(BookEntry.COLUMN_BOOK_PHONE, supplierPhoneString);

            int rowAffected = getContentResolver().update(mCurrentUri, values,
                    null, null);

            if (rowAffected == 0) {
                Toast.makeText(this, "Error with updating book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardDuttonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangeDialog(discardDuttonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangeDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER,
                BookEntry.COLUMN_BOOK_PHONE};

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityNameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);

            bookName.setText(name);
            bookPrice.setText(Integer.toString(price));
            bookQuantity.setText(Integer.toString(quantity));
            bookSupplierName.setText(supplierName);
            bookSupplierPhone.setText(Integer.toString(phone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
        bookSupplierName.setText("");
        bookSupplierPhone.setText("");
    }

    private void showUnsavedChangeDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_all_books));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void deleteBook() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error deleting book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
