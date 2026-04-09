/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2024, 
 * All rights reserved.
 */

package ug.daes.onboarding.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.model.OnbSubscriberDevice;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberDeviceHistoryDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The subscriber details. */
	private OnbSubscriber subscriber;
	
	/** The subscriber current device details. */
	private OnbSubscriberDevice subscriberDevice;
	
	/** The subscriber device history. */
	private List<HashMap<String, String>> subscriberDeviceHistory;
	
	public OnbSubscriber getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(OnbSubscriber subscriber) {
		this.subscriber = subscriber;
	}
	public OnbSubscriberDevice getSubscriberDevice() {
		return subscriberDevice;
	}
	public void setSubscriberDevice(OnbSubscriberDevice subscriberDevice) {
		this.subscriberDevice = subscriberDevice;
	}
	public List<HashMap<String, String>> getSubscriberDeviceHistory() {
		return subscriberDeviceHistory;
	}
	public void setSubscriberDeviceHistory(List<HashMap<String, String>> subscriberDeviceHistory) {
		this.subscriberDeviceHistory = subscriberDeviceHistory;
	}
	@Override
	public String toString() {
		return "SubscriberDeviceHistoryDetails [subscriber=" + subscriber + ", subscriberDevice=" + subscriberDevice
				+ ", subscriberDeviceHistory=" + subscriberDeviceHistory + "]";
	}
}
