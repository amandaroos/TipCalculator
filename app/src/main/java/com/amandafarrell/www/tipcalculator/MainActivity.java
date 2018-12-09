package com.amandafarrell.www.tipcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

    private Integer mDefaultTipPercent = 15;
    private TextView mTipPercentTextView;
    private Integer mTipPercent;
    private String mTipPercentString;
    private Button mTipPercentButtonDecrease;
    private Button mTipPercentButtonIncrease;

    private EditText mTipTotalEditText;
    private Integer mTipTotal;
    private String mTipTotalString;

    private EditText mSplitEditText;
    private Integer mSplit;
    private Button mSplitButtonDecrease;
    private Button mSplitButtonIncrease;

    private TextView mTotalTextView;
    private Integer mTotal;
    private String mTotalString;

    private TextView mPerPersonTextView;
    private TextView mPerPersonLabel;
    private Integer mPerPerson;
    private String mPerPersonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find views
        mBillEditText = (EditText) findViewById(R.id.bill);
        mTipPercentTextView = (TextView) findViewById(R.id.tip_percent);
        mTipPercentButtonDecrease = (Button) findViewById(R.id.button_decrease_tip_percent);
        mTipPercentButtonIncrease = (Button) findViewById(R.id.button_increase_tip_percent);
        mTipTotalEditText = (EditText) findViewById(R.id.tip_total);
        mSplitEditText = (EditText) findViewById(R.id.split);
        mSplitButtonDecrease = (Button) findViewById(R.id.button_decrease_split);
        mSplitButtonIncrease = (Button) findViewById(R.id.button_increase_split);
        mTotalTextView = (TextView) findViewById(R.id.total);
        mPerPersonLabel = (TextView) findViewById(R.id.label_per_person);
        mPerPersonTextView = (TextView) findViewById(R.id.total_per_person);

        //Initialize variables
        mBill = 0;
        mTipPercent = mDefaultTipPercent;
        mTipTotal = 0;
        mSplit = 1;
        mTotal = 0;

        //Set on click listeners
        mTipPercentButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTipPercent > 0) {
                    mTipPercent -= 1;
                    setTipPercentageTextView();
                }
            }
        });

        mTipPercentButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTipPercent < 100) {
                    mTipPercent += 1;
                    setTipPercentageTextView();
                }
            }
        });

        mSplitButtonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSplit > 1) {
                    mSplit -= 1;
                    setSplitEditText();
                }
            }
        });

        mSplitButtonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSplit += 1;
                setSplitEditText();
            }
        });

        //Set text watchers
        mBillEditText.addTextChangedListener(billTextWatcher);
        mTipPercentTextView.addTextChangedListener(tipPercentTextWatcher);
        mTipTotalEditText.addTextChangedListener(tipTotalTextWatcher);
        mSplitEditText.addTextChangedListener(splitTextWatcher);

        //Select all text when selected for each EditText
        mBillEditText.setSelectAllOnFocus(true);
        mTipTotalEditText.setSelectAllOnFocus(true);
        mSplitEditText.setSelectAllOnFocus(true);

        mBillEditText.setFocusable(true);
        setBillEditText();
        setTipTotalEditText();
        setSplitEditText();
        setTotalTextView();
        setPerPersonTextView();
    }

    /**
     * billTextWatcher
     */
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
                String billEditTextContent = mBillEditText.getText().toString();
                String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                billEditTextContent = billEditTextContent.replaceAll(replaceable, "");

                if (billEditTextContent.isEmpty()) {
                    mBill = 0;
                } else {
                    mBill = Integer.parseInt(billEditTextContent);
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), R.string.large_number_error_toast, Toast.LENGTH_LONG).show();

                mBill = 0;
            }

            setBillEditText();
            if (mBillString.substring(mBillString.length() - 1)
                    .equals(NumberFormat.getCurrencyInstance().getCurrency().getSymbol())) {
                mBillEditText.setSelection(mBillEditText.getText().length() - 2);
            } else {
                mBillEditText.setSelection(mBillEditText.getText().length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mBillEditText.hasFocus()){
                calculateTipTotal();
                calculateTotal();
                setTipTotalEditText();
                setTotalTextView();
                setPerPersonTextView();
            }
        }
    };

    /**
     * tipPercentTextWatcher
     */
    private TextWatcher tipPercentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!mTipTotalEditText.hasFocus()){
                calculateTipTotal();
                calculateTotal();
                setTipTotalEditText();
                setTotalTextView();
                setPerPersonTextView();
            }
        }
    };

    /**
     * tipTotalTextWatcher
     */
    private TextWatcher tipTotalTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mTipTotalEditText.hasFocus()){
                if (mIgnoreNextTextChange) {
                    mIgnoreNextTextChange = false;
                    return;
                } else {
                    mIgnoreNextTextChange = true;
                }

                try {
                    String tipTotalEditTextContent = mTipTotalEditText.getText().toString();
                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    tipTotalEditTextContent = tipTotalEditTextContent.replaceAll(replaceable, "");

                    if (tipTotalEditTextContent.isEmpty()) {
                        mTipTotal = 0;
                    } else {
                        mTipTotal = Integer.parseInt(tipTotalEditTextContent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), R.string.large_number_error_toast, Toast.LENGTH_LONG).show();

                    mTipTotal = 0;
                }

                setTipTotalEditText();
                if (mTipTotalString.substring(mTipTotalString.length() - 1)
                        .equals(NumberFormat.getCurrencyInstance().getCurrency().getSymbol())) {
                    mTipTotalEditText.setSelection(mTipTotalEditText.getText().length() - 2);
                } else {
                    mTipTotalEditText.setSelection(mTipTotalEditText.getText().length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//            calculateTipPercentage();
//            setTipPercentageTextView();
            calculateTotal();
            setTotalTextView();
            setPerPersonTextView();
        }
    };

    /**
     * splitTextWatcher
     */
    private TextWatcher splitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try {
                mSplit = Integer.parseInt(mSplitEditText.getText().toString());

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), R.string.large_number_error_toast, Toast.LENGTH_LONG).show();

                mSplit = 1;
                setSplitEditText();
                mSplitEditText.setSelection(mSplitEditText.getText().length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setPerPersonTextView();
        }
    };

    /**
     * Set mBillEditText to current value of mBill
     */
    private void setBillEditText() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mBillString = format.format(mBill / 100.0);
        mBillEditText.setText(mBillString);
    }

    //EditText setting

    /**
     * Set mTipPercentageEditText to current value of mTipPercent
     */
    private void setTipPercentageTextView() {
        mTipPercentString = String.valueOf(mTipPercent) + "%";
        mTipPercentTextView.setText(mTipPercentString);
    }

    /**
     * Set mTipTotalEditText to current value of mTipTotal
     */
    private void setTipTotalEditText() {

        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTipTotalString = format.format(mTipTotal / 100.0);
        mTipTotalEditText.setText(mTipTotalString);
    }

    /**
     * Set mSplitEditText to value of mSplit
     */
    private void setSplitEditText() {
        mSplitEditText.setText(String.valueOf(mSplit));
    }

    /**
     * Set mTotalTextView to current value of mTotal
     */
    private void setTotalTextView() {

        NumberFormat format = NumberFormat.getCurrencyInstance();
        mTotalString = format.format(mTotal / 100.0);
        mTotalTextView.setText(mTotalString);
    }

    /**
     * Set mPerPersonTextView to calculated value of mPerPerson
     */
    private void setPerPersonTextView() {

        if (mSplit > 1) {
            calculatePerPersonTotal();

            NumberFormat format = NumberFormat.getCurrencyInstance();
            mPerPersonString = format.format(mPerPerson / 100.0);
            mPerPersonTextView.setText(mPerPersonString);

            mPerPersonLabel.setVisibility(View.VISIBLE);
            mPerPersonTextView.setVisibility(View.VISIBLE);
        } else {
            mPerPersonLabel.setVisibility(View.GONE);
            mPerPersonTextView.setVisibility(View.GONE);
        }
    }

    //Calculations

    /**
     * Calculate tip percentage based on TipTotal
     */
    private void calculateTipPercentage() {
        if (mBill == 0){
            mTipPercent = mDefaultTipPercent;
        } else {
            mTipPercent = (int) (mTipTotal *1.0 / mBill * 100);
        }
    }

    /**
     * Calculate tip total
     */
    private void calculateTipTotal() {
        mTipTotal = (mBill * mTipPercent) / 100;
    }

    /**
     * Calculate total
     */
    private void calculateTotal() {
        mTotal = mBill + mTipTotal;
    }

    /**
     * Calculate per person total
     */
    private void calculatePerPersonTotal() {
        mPerPerson = mTotal / mSplit;
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reset_tip_percent:
                resetTipPercent();
        }
        return super.onOptionsItemSelected(item);

    }

    private void resetTipPercent(){
        mTipPercent = mDefaultTipPercent;
        setTipPercentageTextView();
    }
}
