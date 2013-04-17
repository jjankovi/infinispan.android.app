package invoices.manager.model;

import java.io.Serializable;

/**
 * 
 * @author jjankovi
 *
 */
public class Invoice implements Serializable {
	
	private static final long serialVersionUID = -7240877323585426820L;
	
	private Integer id;

	private Calendar dateOfIssue;
	
	private Calendar maturityDate;
	
	private long prize;
	
	private String accountNumber;
	
	private String bankCode;
	
	private String notes;
	
	public Invoice() {
		
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Calendar getDateOfIssue() {
		return dateOfIssue;
	}

	public void setDateOfIssue(Calendar dateOfIssue) {
		this.dateOfIssue = dateOfIssue;
	}

	public Calendar getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Calendar maturityDate) {
		this.maturityDate = maturityDate;
	}

	public long getPrize() {
		return prize;
	}

	public void setPrize(long prize) {
		this.prize = prize;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
