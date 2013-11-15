package com.diegofdez.shoplist;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class Item extends Activity {

    // Referencias a elementos en pantalla
    TextView item = null;
    TextView place = null;
    TextView price = null;
    TextView importance = null;

    // Identificador de entrada
    Long rowId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtencion de extras, identificador y accion
        Bundle extras = getIntent().getExtras();
        rowId = (savedInstanceState == null) ?
                null : (Long) savedInstanceState.getSerializable(DataBaseHelper.SL_ID);
        if (rowId == null) {
            rowId = extras != null ? extras.getLong(DataBaseHelper.SL_ID) : null;
        }

        // Modo DETALLE
        if (extras != null && extras.getInt("action") == ItemList.SHOW_ITEM) {
            setContentView(R.layout.detail_item);
        }
        // Modo EDICION
        else {
            setContentView(R.layout.new_item);

            // Boton de salvar
            Button saveBtn = (Button) findViewById(R.id.add);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_OK);
                    saveData();
                    finish();
                }
            });
        }

        // Definir referencias a elementos en plantalla
        item = (TextView) findViewById(R.id.item);
        place = (TextView) findViewById(R.id.place);
        price = (TextView) findViewById(R.id.price);
        importance = (TextView) findViewById(R.id.importance);

        // Identificador visible o no
        TableRow tr = (TableRow) findViewById(R.id.idRow);
        if (rowId != null) {
            tr.setVisibility(View.VISIBLE);
            populateFieldsFromDB();
        }
        else {
            tr.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    protected void saveData() {
        // Obtener datos
        String itemText = item.getText().toString();
        String placeText = place.getText().toString();
        String priceText = price.getText().toString();
        String importanceText = importance.getText().toString();

        if (rowId == null) {
            // Insertar en DB
            ItemList.mDbHelper.insertItem(itemText, placeText, Float.parseFloat(priceText),
                    Integer.parseInt(importanceText));
        }
        else {
            // Actualizar en DB
            TextView tv = (TextView) findViewById(R.id.identificator);
            String ident = tv.getText().toString();
            ItemList.mDbHelper.updateItem(Integer.parseInt(ident), itemText, placeText,
                    Float.parseFloat(priceText), Integer.parseInt(importanceText));
        }
    }

    private void populateFieldsFromDB() {
        Cursor c = ItemList.mDbHelper.getItem(rowId.intValue());
        startManagingCursor(c);
        c.moveToFirst();

        // Obtener datos del cursor
        item.setText(c.getString(c.getColumnIndex(DataBaseHelper.SL_ITEM)));
        place.setText(c.getString(c.getColumnIndexOrThrow(DataBaseHelper.SL_PLACE)));
        price.setText(Float.toString(c.getFloat(2)));
        importance.setText(Integer.toString(c.getInt(3)));
        TextView id = (TextView) findViewById(R.id.identificator);
        id.setText(Integer.toString(c.getInt(4)));
    }
}
