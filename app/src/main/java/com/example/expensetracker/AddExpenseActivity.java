package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {
     DatabaseHelper dbHelper; //object declaration
     EditText descriptionInput, amountInput, categoryInput, dateInput;


    @Override
protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);

        dbHelper = new DatabaseHelper(this);
        descriptionInput = findViewById(R.id.descriptionInput);
        amountInput = findViewById(R.id.amountInput);
        categoryInput = findViewById(R.id.categoryInput);
        dateInput = findViewById(R.id.dateInput);

    dateInput.setOnClickListener(v -> {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddExpenseActivity.this,
                //callback function to set user input date
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String Day = "";
                    if(selectedDay>0 && selectedDay<=9){
                        Day = "0" + Integer.toString(selectedDay);
                    }
                    else{
                        Day = Integer.toString(selectedDay);
                    }
                    String date = selectedYear+ "-" + (selectedMonth + 1) + "-" +  Day  ;
                    dateInput.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    });

    Button saveExpenseButton = findViewById(R.id.saveExpenseButton);
    saveExpenseButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveExpense();
        }
    });

    // Check if an expense ID is passed for updating
    int expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);
    if (expenseId != -1) {
        loadExpenseData(expenseId);
        saveExpenseButton.setText("Update Expense");
    }
}

private void loadExpenseData(int expenseId) {
    Cursor cursor = dbHelper.getExpenseById(expenseId);
    if (cursor.moveToFirst()) {
        descriptionInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)));
        amountInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AMOUNT)));
        categoryInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)));
        dateInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE)));
    }

    cursor.close();
}

public void saveExpense() {
    String description = descriptionInput.getText().toString();
    String amountText = amountInput.getText().toString();
    String category = categoryInput.getText().toString();
    String date = dateInput.getText().toString();

    if (description.isEmpty() || amountText.isEmpty() || category.isEmpty() || date.isEmpty()) {
        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        return;
    }

    double amount = Double.parseDouble(amountText);
    int expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);

    if (expenseId == -1) {
        if (dbHelper.addExpense(description, amount, category, date) != -1) {
            Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
        }
    }
    else {
        if (dbHelper.updateExpense(expenseId, description, amount, category, date)) {
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating expense", Toast.LENGTH_SHORT).show();
        }
    }
}
}
