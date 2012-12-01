package org.infinispan.android.app.model;

import java.io.Serializable;

public class ShopItem implements Serializable {
	
	private static final long serialVersionUID = -7240877323585426820L;

	private String name;
	
	private float prize;

	private String description;
	
	private String manufacturer;
	
	public ShopItem(String name, float prize, String description,
			String manufacturer) {
		super();
		this.name = name;
		this.prize = prize;
		this.description = description;
		this.manufacturer = manufacturer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrize() {
		return prize;
	}

	public void setPrize(float prize) {
		this.prize = prize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	@Override
	public String toString() {
		return name + " " + prize;
	}
}
