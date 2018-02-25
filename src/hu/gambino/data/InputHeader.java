package hu.gambino.data;

import java.sql.Date;

public class InputHeader {
	private Long lineNumber;
	private Long orderItemId;
	private Long orderId;
	private String buyerName;
	private String buyerEmail;
	private String address;
	private Integer postcode;
	private Double salePrice;
	private Double shippingPrice;
	private String sku;
	private OrderItemStatus status;
	private Date orderDate;

	public InputHeader(Long lineNumber, Long orderItemId, Long orderId, String buyerName, String buyerEmail,
			String address, Integer postcode, Double salePrice, Double shippingPrice, String sku,
			OrderItemStatus status, Date orderDate) {
		super();
		this.lineNumber = lineNumber;
		this.orderItemId = orderItemId;
		this.orderId = orderId;
		this.buyerName = buyerName;
		this.buyerEmail = buyerEmail;
		this.address = address;
		this.postcode = postcode;
		this.salePrice = salePrice;
		this.shippingPrice = shippingPrice;
		this.sku = sku;
		this.status = status;
		this.orderDate = orderDate;
	}

	public Long getLineNumber() {
		return lineNumber;
	}

	public Long getOrderItemId() {
		return orderItemId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public String getAddress() {
		return address;
	}

	public Integer getPostcode() {
		return postcode;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public Double getShippingPrice() {
		return shippingPrice;
	}

	public String getSku() {
		return sku;
	}

	public OrderItemStatus getStatus() {
		return status;
	}

	public Date getOrderDate() {
		return orderDate;
	}

}
