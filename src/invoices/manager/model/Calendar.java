package invoices.manager.model;

import java.io.Serializable;

/**
 * 
 * @author jjankovi
 *
 */
public class Calendar implements Serializable {

	private static final long serialVersionUID = -3178048510493941875L;
	
	private int day;
	private int month;
	private int year;
	
	/**
	 * 
	 */
	public Calendar() {
	
	}

	/**
	 * 
	 * @return
	 */
	public int getDay() {
		return day;
	}

	/**
	 * 
	 * @param day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * 
	 * @return
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * 
	 * @param month
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * 
	 * @return
	 */
	public int getYear() {
		return year;
	}

	/**
	 * 
	 * @param year
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString() {
		return year + (month<10?"0":"") + month + (day<10?"0":"") + day;
	}
	
}
