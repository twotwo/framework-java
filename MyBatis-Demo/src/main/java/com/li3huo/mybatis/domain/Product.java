/**
 * 
 */
package com.li3huo.mybatis.domain;

/**
 * @author liyan
 * 
 */
public class Product {
	private String productid;
	private String category;
	private String name;
	private String descn;
	/**
	 * @return the productid
	 */
	public String getProductid() {
		return productid;
	}
	/**
	 * @param productid the productid to set
	 */
	public void setProductid(String productid) {
		this.productid = productid;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the descn
	 */
	public String getDescn() {
		return descn;
	}
	/**
	 * @param descn the descn to set
	 */
	public void setDescn(String descn) {
		this.descn = descn;
	}
	
}
