package com.diegofdez.shoplist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by diegofdez on 10/11/13.
 */
public class ShopAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;
    private Cursor cursor;

    public ShopAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        cursor = c;
        cursor.moveToFirst();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Comprobamos si hay que iniciar el cursor o no
        if (cursor.getPosition() < 0) {
            cursor.moveToFirst();
        }
        else {
            cursor.moveToPosition(position);
        }

        // Obtener la vista de la línea de la tabla
        View row = mInflater.inflate(R.layout.mainlist_row, null);

        // Rellenar datos
        TextView place = (TextView) row.findViewById(R.id.row_place);
        TextView item = (TextView) row.findViewById(R.id.row_item);
        place.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.SL_PLACE)));
        item.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.SL_ITEM)));

        int importance = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.SL_IMPORTANCE));
        int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.SL_ID));

        // Se escoge un icono en base a la importancia
        ImageView icon = (ImageView) row.findViewById(R.id.row_importance);
        icon.setTag(new Integer(rowId));
        switch (importance) {
            case 1:
                icon.setImageResource(R.drawable.icon1);
                break;
            case 2:
                icon.setImageResource(R.drawable.icon2);
                break;
            default:
                icon.setImageResource(R.drawable.icon3);
                break;
        }
        return row;
    }
}
