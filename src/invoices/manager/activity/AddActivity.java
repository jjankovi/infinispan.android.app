package invoices.manager.activity;

import invoices.manager.model.Calendar;
import invoices.manager.model.Invoice;

import java.io.Serializable;
import java.math.BigInteger;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author jjankovi
 *
 */
public class AddActivity extends FragmentActivity {

	private EditText dateOfIssue;
	private EditText maturityDate;
	private EditText name;
	private EditText street;
	private EditText city;
	private EditText prize;
	private TextView invoice;
	private TextView invoiceNumber;
	private TextView error;
	
	private static final int DATE_OF_ISSUE_ID = 1212;
	private static final int MATURITY_DATE_ID = 1213;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		setViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}

	/** Called when the cancel button is pressed -> go back to main activity **/
	public void cancel(View view) {
		finish();
	}

	/** Called when the add button is pressed **/
	public void save(View view) {
		Intent intent = getIntent();

		int[] dateOfIssueCalendar = getSingleDateElements(dateOfIssue.getText().toString());
		int[] maturityDateCalendar = getSingleDateElements(maturityDate.getText().toString());
		
		Calendar dateOfIssue = new Calendar();
		dateOfIssue.setDay(dateOfIssueCalendar[0]);
		dateOfIssue.setMonth(dateOfIssueCalendar[1]);
		dateOfIssue.setYear(dateOfIssueCalendar[2]);
		
		Calendar maturityDate = new Calendar();
		maturityDate.setDay(maturityDateCalendar[0]);
		maturityDate.setMonth(maturityDateCalendar[1]);
		maturityDate.setYear(maturityDateCalendar[2]);
		
		/* validation */
		String errorText = null;
		if (name.getText().toString().equals("") ||
			name.getText().toString() == null) {
			errorText = "Name must be set!";
		}
		if (prize.getText().toString() == null ||
			prize.getText().toString().equals("")) {
			errorText = "Prize must be set!";
		}
		BigInteger issueDateBigInteger = new BigInteger(dateOfIssue.toString());
		BigInteger maturityDateBigInteger = new BigInteger(maturityDate.toString());
		if (issueDateBigInteger.compareTo(maturityDateBigInteger) == 1) {
			errorText = "Date of issue must be greater than maturity date!";
		}
			
		if (errorText != null) {
			error.setText(errorText);
			error.setTextColor(Color.RED);
			error.setVisibility(View.VISIBLE);
			return;
		}
		/* end of validation */
		
		Invoice invoiceItem = new Invoice();
		invoiceItem.setId(12);
		invoiceItem.setDateOfIssue(dateOfIssue);
		invoiceItem.setMaturityDate(maturityDate);
		invoiceItem.setPrize(Long.parseLong(prize.getText().toString()));
		invoiceItem.setName(name.getText().toString());
		invoiceItem.setStreet(street.getText().toString());
		invoiceItem.setCity(city.getText().toString());

		intent.putExtra("item", invoiceItem);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void setDateOfIssue(View view) {
		int[] dateElements = getSingleDateElements(dateOfIssue.getText()
				.toString());
		DialogFragment dialogFragment = new DatePickerFragment(
				DATE_OF_ISSUE_ID, dateElements[2], dateElements[1] - 1,
				dateElements[0]);
		dialogFragment.show(getSupportFragmentManager(), "dateOfIssuePicker");
	}

	public void setMaturityDate(View view) {
		int[] dateElements = getSingleDateElements(maturityDate.getText()
				.toString());
		DialogFragment dialogFragment = new DatePickerFragment(
				MATURITY_DATE_ID, dateElements[2], dateElements[1] - 1,
				dateElements[0]);
		dialogFragment.show(getSupportFragmentManager(), "maturityDatePicker");
	}

	private void setViews() {
		setDefaultsValues();
		setValuesFromIntent();
	}

	private void setDefaultsValues() {
		error = (TextView) findViewById(R.id.errorAddInvoice);
		error.setVisibility(View.GONE);
		
		dateOfIssue = (EditText) findViewById(R.id.dateOfIssue);
		dateOfIssue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) setDateOfIssue(v);
			}
		});
		dateOfIssue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setDateOfIssue(v);
			}
		});
			
		setCurrentDateOnTextView(dateOfIssue);
		maturityDate = (EditText) findViewById(R.id.maturityDate);
		maturityDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) setMaturityDate(v);
			}
		});
		maturityDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setMaturityDate(v);
			}
		});
		setCurrentDateOnTextView(maturityDate);

		name = (EditText) findViewById(R.id.name);
		street = (EditText) findViewById(R.id.street);
		city = (EditText) findViewById(R.id.city);
		prize = (EditText) findViewById(R.id.prize);
		prize.setText("0");
		
		invoice = (TextView) findViewById(R.id.invoice);
		invoice.setVisibility(View.GONE);
		invoiceNumber = (TextView) findViewById(R.id.invoiceNumber);
		invoiceNumber.setVisibility(View.GONE);
		
	}
	
	private void setValuesFromIntent() {
		Intent intent = getIntent();

		Serializable serializableItem = null;
		if (intent.getExtras() != null) {
			serializableItem = intent.getExtras().getSerializable("item");
		}

		if (serializableItem != null) {
			Invoice invoiceItem = (Invoice) serializableItem;

			name.setText(invoiceItem.getName());
			street.setText(invoiceItem.getStreet());
			city.setText(invoiceItem.getCity());
			prize.setText(invoiceItem.getPrize() + "");
			invoice.setText(R.string.id);
			invoice.setVisibility(View.VISIBLE);
			invoiceNumber.setText(invoiceItem.getId() + "");
			invoiceNumber.setVisibility(View.VISIBLE);
			
			Calendar issue = invoiceItem.getDateOfIssue();
			Calendar maturity = invoiceItem.getMaturityDate();
			
			setDateOnTextView(dateOfIssue, issue.getDay(), issue.getMonth(), issue.getYear());
			setDateOnTextView(maturityDate, maturity.getDay(), maturity.getMonth(), maturity.getYear());
		}
	}

	private void setCurrentDateOnTextView(TextView view) {
		final java.util.Calendar c = java.util.Calendar.getInstance();
		int year = c.get(java.util.Calendar.YEAR);
		int month = c.get(java.util.Calendar.MONTH) + 1;
		int day = c.get(java.util.Calendar.DAY_OF_MONTH);
		setDateOnTextView(view, day, month, year);
	}

	private void setDateOnTextView(TextView view, int day, int month, int year) {
		view.setText((day<10?"0":"") + day + "." + (month<10?"0":"") + month + "." + year);
	}

	private int[] getSingleDateElements(String text) {
		String[] stringDateElements = text.split("\\.");
		int[] dateElements = new int[stringDateElements.length];
		for (int i = 0; i < 3; i++) {
			dateElements[i] = Integer.parseInt(stringDateElements[i]);
		}
		return dateElements;
	}

	@SuppressLint({ "ValidFragment" })
	private class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		private int id;
		private int year;
		private int month;
		private int day;

		public DatePickerFragment(int id, int year, int month, int day) {
			this.id = id;
			this.year = year;
			this.month = month;
			this.day = day;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			switch (id) {
			case DATE_OF_ISSUE_ID:
				setDateOnTextView(dateOfIssue, day, month + 1, year);
				break;
			case MATURITY_DATE_ID:
				setDateOnTextView(maturityDate, day, month + 1, year);
				break;
			}
		}
	}

}
