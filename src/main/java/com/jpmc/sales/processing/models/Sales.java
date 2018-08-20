package com.jpmc.sales.processing.models;

import java.math.BigDecimal;

public class Sales {
	private Product product;
	private int saleCount;
	private BigDecimal saleTotal;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(int saleCount) {
		this.saleCount = saleCount;
	}
	public BigDecimal getSaleTotal() {
		return saleTotal;
	}
	public void setSaleTotal(BigDecimal saleTotal) {
		this.saleTotal = saleTotal;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + saleCount;
		result = prime * result + ((saleTotal == null) ? 0 : saleTotal.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sales other = (Sales) obj;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (saleCount != other.saleCount)
			return false;
		if (saleTotal == null) {
			if (other.saleTotal != null)
				return false;
		} else if (!saleTotal.equals(other.saleTotal))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "{productName: "+this.getProduct().getProductName()+","
				+ " productRate: "+this.getProduct().getProductRate()+","
				+ " sale: "+this.getSaleCount()+","
				+ " saleTotal: "+this.getSaleTotal()+"}\n";
	}
}
