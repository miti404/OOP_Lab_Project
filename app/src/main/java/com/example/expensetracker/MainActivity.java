package com.example.expensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView expensesList;
    TextView totalExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        expensesList = findViewById(R.id.expenseListView);
        totalExpense = findViewById(R.id.totalExpenseView);

        dbHelper = new DatabaseHelper(this);

        fetchExpenseList();

    }

    public void fetchExpenseList() {
        double total = dbHelper.getTotalExpenses();
        totalExpense.setText("Total Expense: " + total + " Taka");

        Cursor cursor = dbHelper.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Record Added", Toast.LENGTH_LONG).show();
        }

        String[] columns = new String[]{
                DatabaseHelper.COL_DESCRIPTION,
                DatabaseHelper.COL_AMOUNT,
                DatabaseHelper.COL_CATEGORY,
                DatabaseHelper.COL_DATE
        };

        int[] to = new int[]{
                R.id.expenseDescription,
                R.id.expenseAmount,
                R.id.expenseCategory,
                R.id.expenseDate
        };


        if (expensesList.getAdapter() == null) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_layout, cursor, columns, to, 0) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    Button deleteButton = view.findViewById(R.id.deleteButton);
                    Button editButton = view.findViewById(R.id.editButton);

                    deleteButton.setOnClickListener(v -> {
                        Cursor c = (Cursor) getItem(position);
                        int expenseId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        if (dbHelper.deleteExpense(expenseId)) {
                            Toast.makeText(MainActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                            fetchExpenseList();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error deleting expense", Toast.LENGTH_SHORT).show();
                        }
                    });

                    editButton.setOnClickListener(v -> {
                        Cursor c = (Cursor) getItem(position);
                        int expenseId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
                        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                        intent.putExtra("EXPENSE_ID", expenseId); //passing id from main activity to AddExpense activity
                        startActivity(intent);
                    });

                    return view;
                }
            };
            expensesList.setAdapter(adapter);
        } else {
            // Update the existing adapter's cursor (for db change)
            ((SimpleCursorAdapter) expensesList.getAdapter()).swapCursor(cursor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume(); //restart the prev activity
        fetchExpenseList();
    }

    //Add button
    public void gotoAddPage(View view) {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }
}