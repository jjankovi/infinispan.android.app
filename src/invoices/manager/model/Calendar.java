package invoices.manager.model;

import java.io.Serializable;

/**
 * Custom Calendar object which is using in model of application 
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
	 * Creates Calendar object
	 */
	public Calendar() {
	
	}

	/**
	 * Day value of date 
	 * 
	 * @return		day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Sets day parameter with given value
	 * 
	 * @param 		day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Month value of date
	 * 
	 * @return		month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Sets month parameter with given value
	 * 
	 * @param 		month
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * Year value of date
	 * 
	 * @return		year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Sets year parameter with given value
	 * 
	 * @param 		year
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * Returns date in format dd.mm.yyyy
	 * 
	 * @return
	 */
	public String getFormattedDate() {
		return (day<10?"0":"") + day + "." + (month<10?"0":"") + month + "." + year;
	}
	
	@Override
	public String toString() {
		return year + (month<10?"0":"") + month + (day<10?"0":"") + day;
	}
	
}
