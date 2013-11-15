package com.diegofdez.shoplist;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;

public class ItemList extends ListActivity {

    // Acciones sobre elementos
    public static final int NEW_ITEM = 1;
    public static final int EDIT_ITEM = 2;
    public static final int SHOW_ITEM = 3;

    // Elemento seleccionado
    private long lastRowSelected = 0;
    public static DataBaseHelper mDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Abrir la base de datos
        mDbHelper = new DataBaseHelper(this);
        try {
            mDbHelper.open();
            fillData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_item:
                Intent intent = new Intent(this, Item.class);
                startActivityForResult(intent, NEW_ITEM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillData() {
        Cursor itemCursor = null;
        itemCursor = mDbHelper.getItems();

        // TODO Reemplazar esto por CursorLoader
        // referencia: http://stackoverflow.com/questions/10145735/how-to-properly-transition-from-startmanagingcursor-to-cursorloader
        startManagingCursor(itemCursor);

        // Array de campos FROM del curso: Los de la TABLA en base de datos
        String[] from = new String[] {
                DataBaseHelper.SL_PLACE,
                DataBaseHelper.SL_ITEM,
                DataBaseHelper.SL_IMPORTANCE,
                DataBaseHelper.SL_ID
        };
        // Array de elementos que mostrarán la información. LAs VIEW de la activity.
        int[] to = new int[] {
                R.id.row_item,
                R.id.row_place,
                R.id.row_importance
        };

        // Adaptador que mostrará la información
        ShopAdapter items = new ShopAdapter(this, R.layout.mainlist_row, itemCursor, from, to);

        // Asignar adaptador a la lista
        setListAdapter(items);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shop_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo delW = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Salvar identificador del elemento pulsado
        lastRowSelected = delW.id;

        // Comprobar el elemento seleccionado
        switch (item.getItemId()) {
            case R.id.delete_item:
                // Preguntar si esta seguro de borrarlo
                new AlertDialog.Builder(this)
                        .setTitle(this.getString(R.string.alrtDelete))
                        .setMessage(R.string.alrtDeleteEntry)
                        .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteEntry();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            case R.id.edit_item:
                // Nueva actividad con el identificador como parametro
                Intent i = new Intent(this, Item.class);
                i.putExtra(DataBaseHelper.SL_ID, lastRowSelected);
                startActivityForResult(i, EDIT_ITEM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(this, Item.class);
        i.putExtra(DataBaseHelper.SL_ID, id);
        i.putExtra("action", SHOW_ITEM);
        startActivityForResult(i, SHOW_ITEM);
    }

    private void deleteEntry() {
        mDbHelper.delete(lastRowSelected);
        fillData();
    }
}
