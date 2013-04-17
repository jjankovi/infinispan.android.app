package invoices.manager.adapter;

import invoices.manager.activity.R;
import invoices.manager.model.Invoice;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author jjankovi
 *
 */
public class InvoiceListAdapter extends ArrayAdapter<Invoice> {

	private List<Invoice> invoices;
	private Context context;

	/**
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
	 * 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_item_row, parent,
					false);
		}
		// Now we can fill the layout with the right values
		TextView itemRowAccount = (TextView) convertView.findViewById(R.id.item_row_account);
		TextView itemRowDate = (TextView) convertView.findViewById(R.id.item_row_date);
		TextView itemRowPrize = (TextView) convertView.findViewById(R.id.item_row_prize);
		itemRowPrize.setTextColor(Color.RED);
		
		Invoice invoice = invoices.get(position);

		itemRowAccount.setText(invoice.getAccountNumber() + "/" + invoice.getBankCode());
		itemRowDate.setText(invoice.getDateOfIssue().toString());
		itemRowPrize.setText(invoice.getPrize() + "€");
		
		return convertView;
	}

}
