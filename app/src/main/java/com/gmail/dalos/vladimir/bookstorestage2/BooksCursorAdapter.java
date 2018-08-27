package com.gmail.dalos.vladimir.bookstorestage2;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gmail.dalos.vladimir.bookstorestage2.data.BookContract.BookEntry;

public class BooksCursorAdapter extends CursorAdapter {

    @BindView(R.id.tv_book_name)
    TextView tvBookName;
    @BindView(R.id.tv_book_quantity)
    TextView tvBookQuantity;
    @BindView(R.id.tv_book_price)
    TextView tvBookPrice;

    @BindView(R.id.btn_sale)
    Button buttonSale;

    public BooksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).
                inflate(R.layout.item_card_view, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        ButterKnife.bind(this, view);

        final int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);

        final String bookId = cursor.getString(idColumnIndex);
        String bookName = cursor.getString(nameColumnIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);
        String priceColumn = cursor.getString(priceColumnIndex);

        tvBookName.setText(bookName);
        tvBookQuantity.setText(String.valueOf(bookQuantity));
        tvBookPrice.setText(String.valueOf(priceColumn));

        buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "Button Sale");
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.buttonSale(Integer.valueOf(bookId), Integer.valueOf(bookQuantity));
            }

        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "On Click Detail Activity");
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(
                        BookEntry.CONTENT_URI,
                        Long.parseLong(bookId));
                intent.setData(currentBookUri);
                context.startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("clicked", "On Long Clicked Editor Activity");

                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(
                        BookEntry.CONTENT_URI,
                        Long.parseLong(bookId));

                intent.setData(currentBookUri);
                context.startActivity(intent);

                return true;
            }
        });
    }
}