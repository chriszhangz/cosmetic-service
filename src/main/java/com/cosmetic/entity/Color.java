package com.cosmetic.entity;

import java.util.List;



public class Color {
	
	private String color;
	private String product_sku;

    private List<ColorCode> colorCodes;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getProduct_sku() {
		return product_sku;
	}

	public void setProduct_sku(String product_sku) {
		this.product_sku = product_sku;
	}

	public List<ColorCode> getColorCodes() {
		return colorCodes;
	}

	public void setColorCodes(List<ColorCode> colorCodes) {
		this.colorCodes = colorCodes;
	}


	

}
