package com.gmail.dalos.vladimir.bookstorestage1.data;

import android.provider.BaseColumns;

public class BookContract {

    private BookContract() {
    }

    public static final class BookEntry implements BaseColumns {

        public final static String TABLE_NAME = "books";
        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "product_name";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER = "supplier_name";
        public final static String COLUMN_BOOK_PHONE = "supplier_phone_number";

    }
}
