package com.estafet.iot.billing.models;

public class DeviceBilling {

	private String deviceName;
	private int days;
	private int costs;

	public DeviceBilling() {
	}

	public DeviceBilling(String deviceName, int days, int costs) {
		this.deviceName = deviceName;
		this.days = days;
		this.costs = costs;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getCosts() {
		return costs;
	}

	public void setCosts(int costs) {
		this.costs = costs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceBilling) {
			DeviceBilling billing = (DeviceBilling) obj;
			if (billing.getDeviceName().equals(this.getDeviceName())) {
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

}