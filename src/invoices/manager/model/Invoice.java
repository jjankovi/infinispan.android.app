package invoices.manager.model;

import java.io.Serializable;

/**
 * Stands for Model object in MVC model
 * 
 * @author jjankovi
 *
 */
public class Invoice implements Serializable {
	
	private static final long serialVersionUID = -7240877323585426820L;
	
	private Integer id;

	private Calendar dateOfIssue;
	
	private Calendar maturityDate;
	
	private long price;
	
	private String accountNumber;
	
	private String bankCode;
	
	private String notes;
	
	/**
	 * Constructs a new object of Invoice
	 */
	public Invoice() {
		
	}
	
	/**
	 * Returns a value of id
	 * 
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets a value of id
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns a date of issue of invoice
	 * 
	 * @return
	 */
	public Calendar getDateOfIssue() {
		return dateOfIssue;
	}

	/**
	 * Sets a date of issue
	 * 
	 * @param dateOfIssue
	 */
	public void setDateOfIssue(Calendar dateOfIssue) {
		this.dateOfIssue = dateOfIssue;
	}

	/**
	 * Returns a maturity date of invoice
	 * 
	 * @return
	 */
	public Calendar getMaturityDate() {
		return maturityDate;
	}

	/**
	 * Sets a maturity date
	 * 
	 * @param maturityDate
	 */
	public void setMaturityDate(Calendar maturityDate) {
		this.maturityDate = maturityDate;
	}

	/**
	 * Returns an invoice price
	 * 
	 * @return
	 */
	public long getPrice() {
		return price;
	}

	/**
	 * Sets an invoice price
	 * 
	 * @param price
	 */
	public void setPrice(long price) {
		this.price = price;
	}

	/**
	 * Returns a bank account number
	 * 
	 * @return
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Sets a bank account number
	 * 
	 * @param accountNumber
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Returns a bank code number
	 * 
	 * @return
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * Sets a bank code number
	 * 
	 * @param bankCode
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * Returns the notes
	 * 
	 * @return
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the notes
	 * 
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

}
