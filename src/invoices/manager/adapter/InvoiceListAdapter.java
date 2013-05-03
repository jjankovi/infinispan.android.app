package invoices.manager.adapter;

import invoices.manager.activity.R;
import invoices.manager.model.Invoice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Custom List adapter used in Invoices activity
 * 
 * @author jjankovi
 * 
 */
public class InvoiceListAdapter extends ArrayAdapter<Invoice> {

	private List<Invoice> invoices;
	private Context context;
	private ArrayList<Integer> selectedIds = new ArrayList<Integer>();

	/**
	 * Constructs InvoiceListAdapter with given list of Invoice element and 
	 * application context
	 * 
	 * @param invoices
	 * @param context
	 */
	public InvoiceListAdapter(List<Invoice> invoices, Context context) {
		super(context, R.layout.listview_item_row, invoices);
		this.invoices = invoices;
		this.context = context;
	}

	/**
	 * List Item with given position will be selected 
	 *
	 * @param position
	 */
	public void select(Integer position) {
		if (!selectedIds.contains(position)) {
			selectedIds.add(position);
		}
	}
	
	/**
	 * List Item with given position will be deselected
	 * 
	 * @param position
	 */
	public void deselect(Integer position) {
		if (selectedIds.contains(position)) {
			selectedIds.remove(position);
		}
	}
	
	/**
	 * List Item with given position will be toggled
	 * 
	 * @param position
	 */
	public void toggleSelected(Integer position) {
		if (selectedIds.contains(position)) {
			selectedIds.remove(position);
		} else {
			selectedIds.add(position);
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// First let's verify the convertView is null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_item_row, parent,
					false);
		}

		// Now we can fill the layout with the right values
		TextView itemRowAccount = (TextView) convertView
				.findViewById(R.id.item_row_account);
		TextView itemRowDate = (TextView) convertView
				.findViewById(R.id.item_row_date);
		TextView itemRowPrize = (TextView) convertView
				.findViewById(R.id.item_row_prize);
		itemRowPrize.setTextColor(Color.RED);

		Invoice invoice = invoices.get(position);

		itemRowAccount.setText(invoice.getAccountNumber() 
				+ "/" 
				+ invoice.getBankCode());
		itemRowDate.setText(invoice.getDateOfIssue().getFormattedDate());
		itemRowPrize.setText(invoice.getPrice() + "€");

		if (selectedIds.contains(position)) {
			convertView.setSelected(true);
			convertView.setPressed(true);
			convertView.setBackgroundResource(R.color.blue2);
		} else {
			convertView.setSelected(false);
			convertView.setPressed(false);
			convertView.setBackgroundResource(0);
		}
		return convertView;
	}

}
