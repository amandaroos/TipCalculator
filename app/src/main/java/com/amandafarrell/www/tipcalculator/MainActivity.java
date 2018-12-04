package com.amandafarrell.www.tipcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private Boolean mIgnoreNextTextChange = false;

    private EditText mBillEditText;
    private Integer mBill;
    private String mBillString;

    private EditText mTipPercentEditText;
    private Integer mTipPercent;
    private String mTipPercentString;
    private Button mTipPercentButtonIncrease;
    private Button mTipPercentButtonDecrease;

    private EditText mTipTotalEditText;
    private Integer mTipTotal;
    private String mTipTotalString;

    private TextView mTotalTextView;
    private Integer mTotal;
    private String mTotalString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find views
        mBillEditText = (EditText) findViewById(R.id.bill);
        mTipPercentEditText = (EditText) findViewById(R.id.tip_percent);
        mTipPercentButtonIncrease = (Button) findViewById(R.id.button_increase_tip_percent);
        mTipPercentButtonDecrease = (Button) findViewById(R.id.button_decrease_tip_percent);
        mTipTotalEditText = (EditText) findViewById(R.id.tip_total);
        mTotalTextView = (TextView) findViewById(R.id.total);

        //Initialize variables
        mBill = 0;
        mTipPercent = 15;
        mTipTotal = 0;
        mTotal = 0;

        //Set on click listeners
        mTipPercentButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTipPercent > 0) {
                    mTipPercent -= 1;
                    setTipPercentageEditText();
                }
            }
        });

        mTipPercentButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTipPercent < 100) {
                    mTipPercent += 1;
                    setTipPercentageEditText();
                }
            }
        });

        //Set text watchers
        mBillEditText.addTextChangedListener(billTextWatcher);

        //Select all text when selected for each EditText
        mBillEditText.setSelectAllOnFocus(true);

        setBillEditText();
    }

    private TextWatcher billTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mIgnoreNextTextChange) {
                mIgnoreNextTextChange = false;
                return;
            } else {
                mIgnoreNextTextChange = true;
            }

            try {
                String billStr = mBillEditText.getText().toString();
                String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                billStr = billStr.replaceAll(replaceable, "");

                if (billStr.isEmpty()) {
                    mBill = 0;
                } else {
                    mBill = Integer.parseInt(billStr);
                }
            }
            catch (Exception e){
                Toast.makeText(getBaseContext(), R.string.large_number_error_toast, Toast.LENGTH_LONG).show();

                mBill = 0;
            }

            setBillEditText();
            if (mBillString.substring(mBillString.length()- 1 )
                    .equals(NumberFormat.getCurrencyInstance().getCurrency().getSymbol())){
                mBillEditText.setSelection(mBillEditText.getText().length() - 2);
            } else {
                mBillEditText.setSelection(mBillEditText.getText().length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setTotalEditText();
            setTipTotalEditText();
        }
    };

    /**
     * Set mBillEditText to current value of mBill
     */
    private void setBillEditText () {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mBillString = format.format(mBill/100.0);
        mBillEditText.setText(mBillString);
    }

    /**
     * Set mTipPercentageEditText to current value of mTipPercent
     */
    private void setTipPercentageEditText () {
        mTipPercentString = String.valueOf(mTipPercent) + "%";
        mTipPercentEditText.setText(mTipPercentString);
    }

    /**
     * Set mTipTotalEditText to current value of mTipTotal
     */
    private void setTipTotalEditText () {

        calculateTipTotal();

        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTipTotalString = format.format(mTipTotal/100.0);
        mTipTotalEditText.setText(mTipTotalString);
    }

    /**
     * Set mTotalEditText to calculated value of mTotal
     */
    private void setTotalEditText () {

        calculateTotal();

        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTotalString = format.format(mTotal/100.0);
        mTotalTextView.setText(mTotalString);
    }

    /**
     * Calculate tip total
     */
    private void calculateTipTotal () {
        mTipTotal = (mBill * mTipPercent)/100;
    }

    /**
     * Calculate total
     */
    private void calculateTotal () {
        mTotal = mBill + mTipTotal;
    }
}
