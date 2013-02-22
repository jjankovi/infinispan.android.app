package invoices.manager.model;

import java.io.Serializable;
import java.util.Calendar;

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
	
	private String name;
	
	private String street;
	
	private String city;
	
	public Invoice(Integer id, Calendar dateOfIssue, Calendar maturityDate,
			long prize, String name, String street, String city) {
		super();
		this.id = id;
		this.dateOfIssue = dateOfIssue;
		this.maturityDate = maturityDate;
		this.prize = prize;
		this.name = name;
		this.street = street;
		this.city = city;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
