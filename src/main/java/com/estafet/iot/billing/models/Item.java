package com.estafet.iot.billing.models;

public class Item {

	private String id;
	private String customerId;
	private String thingName;
	private String thingType;
	private String sn;
	private boolean own;
	private String validFrom;
	private String validTo;
	
	public Item () {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public String getThingType() {
		return thingType;
	}

	public void setThingType(String thingType) {
		this.thingType = thingType;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public boolean isOwn() {
		return own;
	}

	public void setOwn(boolean own) {
		this.own = own;
	}

	public String getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	public String getValidTo() {
		return validTo;
	}

	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

		@Override
		public String toString() {
				return "Item{" +
								"id='" + id + '\'' +
								", customerId='" + customerId + '\'' +
								", thingName='" + thingName + '\'' +
								", thingType='" + thingType + '\'' +
								", sn='" + sn + '\'' +
								", own=" + own +
								", validFrom='" + validFrom + '\'' +
								", validTo='" + validTo + '\'' +
								'}';
		}
}
