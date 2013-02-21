package invoices.manager.activity;

import invoices.manager.model.Invoice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author jjankovi
 *
 */
public class AddActivity extends FragmentActivity {

	private Button dateOfIssue;
	private Button maturityDate;
	private EditText name;
	private EditText street;
	private EditText city;
	private EditText prize;
	private TextView invoice;
	private TextView invoiceNumber;

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
		Invoice invoiceItem = new Invoice(12, 
				new GregorianCalendar(dateOfIssueCalendar[2], dateOfIssueCalendar[1], 
						dateOfIssueCalendar[0]), new GregorianCalendar(maturityDateCalendar[2], 
						maturityDateCalendar[1], maturityDateCalendar[0]), 
						Long.parseLong(prize.getText().toString()), name.getText().toString(), 
						street.getText().toString(), city.getText().toString());
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
		dateOfIssue = (Button) findViewById(R.id.dateOfIssue);
		setCurrentDateOnTextView(dateOfIssue);
		maturityDate = (Button) findViewById(R.id.maturityDate);
		setCurrentDateOnTextView(maturityDate);

		name = (EditText) findViewById(R.id.name);
		street = (EditText) findViewById(R.id.street);
		city = (EditText) findViewById(R.id.city);
		prize = (EditText) findViewById(R.id.prize);

		invoice = (TextView) findViewById(R.id.invoice);
		invoice.setText("Create a new invoice");
		invoiceNumber = (TextView) findViewById(R.id.invoiceNumber);
		invoiceNumber.setText("");
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
			invoice.setText(R.string.invoice);
			invoiceNumber.setText(invoiceItem.getId() + "");
			
			int month = invoiceItem.getDateOfIssue().get(Calendar.MONTH);
			invoiceItem.getDateOfIssue().set(Calendar.MONTH, month - 1);
			month = invoiceItem.getMaturityDate().get(Calendar.MONTH);
			invoiceItem.getMaturityDate().set(Calendar.MONTH, month - 1);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
			dateOfIssue.setText(simpleDateFormat.format(
					invoiceItem.getDateOfIssue().getTime()));
			maturityDate.setText(simpleDateFormat.format(
					invoiceItem.getMaturityDate().getTime()));
		}
	}

	private void setCurrentDateOnTextView(TextView view) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		setDateOnTextView(view, year, month, day);
	}

	private void setDateOnTextView(TextView view, int year, int month, int day) {
		view.setText(day + "." + month + "." + year);
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
				setDateOnTextView(dateOfIssue, year, month + 1, day);
				break;
			case MATURITY_DATE_ID:
				setDateOnTextView(maturityDate, year, month + 1, day);
				break;
			}
		}
	}

}
