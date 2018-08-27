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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dalos.vladimir.bookstorestage2.data.BookContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    @BindView(R.id.tv_details_name)
    TextView detailsName;
    @BindView(R.id.tv_details_price)
    TextView detailPrice;
    @BindView(R.id.tv_details_quantity)
    TextView detailsQuantity;
    @BindView(R.id.tv_details_supplier_name)
    TextView detailsSupplierName;
    @BindView(R.id.tv_details_supplier_phone)
    TextView detailsSupplierPhone;
    int idColumnIndex;
    int quantity;
    int supplierPhone;
    private Uri mCurrentBookUri;

    @OnClick(R.id.btn_details_quantity_up)
    public void clickQuantityUp(View view) {
        buttonAdd(idColumnIndex, quantity);
    }

    @OnClick(R.id.btn_details_quantity_down)
    public void clickQuantityDown(View view) {
        buttonMinus(idColumnIndex, quantity);
    }

    @OnClick(R.id.btn_details_call_supplier)
    public void clickPhone(View view) {
        String phone = String.valueOf(supplierPhone);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                phone, null));
        startActivity(intent);
    }

    @OnClick(R.id.fab_details)
    public void onClick(View view) {
        showDeleteConfirmationDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            invalidateOptionsMenu();
        } else {
            setTitle("Details");
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER,
                    null, this);
        }
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
                mCurrentBookUri,
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

            idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityNameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            supplierPhone = cursor.getInt(phoneColumnIndex);

            detailsName.setText(name);
            detailPrice.setText(Integer.toString(price));
            detailsQuantity.setText(Integer.toString(quantity));
            detailsSupplierName.setText(supplierName);
            detailsSupplierPhone.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void buttonAdd(int bookId, int bookQuantity) {
        bookQuantity += 1;
        if (bookQuantity >= 0) {
            updateBook(bookQuantity);
            Toast.makeText(this, "Quantity increased", Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonMinus(int bookId, int bookQuantity) {
        bookQuantity -= 1;
        if (bookQuantity >= 0) {
            updateBook(bookQuantity);
            Toast.makeText(this, "Quantity decreased", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No more product", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBook(int bookQuantity) {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insert successful", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowAffected = getContentResolver().update(mCurrentBookUri, values,
                    null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowDeleted = getContentResolver().delete(mCurrentBookUri,
                    null, null);
            if (rowDeleted == 0) {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete successful", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Book?");
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
}
