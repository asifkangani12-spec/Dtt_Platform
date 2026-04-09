package ug.daes.onboarding.util;

import com.dtt.common.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;

import ug.daes.onboarding.model.OnbSubscriber;
import ug.daes.onboarding.repository.SubscriberRepoIface;

public class VersionComparatorThread implements Runnable {

	private OnbSubscriber subscriber;
	private SubscriberRepoIface subscriberRepoIface;
	private static String newVersion;
	private static String currentVersion;
	private static String osVersion;
	private HttpServletRequest httpServletRequest;

	public VersionComparatorThread(OnbSubscriber subscriber, SubscriberRepoIface subscriberRepoIface,
								   HttpServletRequest httpServletRequest) {
		this.subscriber = subscriber;
		this.subscriberRepoIface = subscriberRepoIface;
		this.httpServletRequest = httpServletRequest;
	}

	@Override
	public void run() {
		try {

			if (isNewerVersion(httpServletRequest.getHeader("appVersion"), subscriber.getAppVersion())) {
				System.out.println("New version is greater");
				subscriber.setAppVersion(httpServletRequest.getHeader("appVersion"));
				subscriber.setOsVersion(httpServletRequest.getHeader("osVersion"));
				subscriber.setUpdatedDate(AppUtil.getDate());
				subscriberRepoIface.save(subscriber);
			} else {
				System.out.println("Current version is up-to-date or newer");
			}

		} catch (Exception e) {
			System.out.println("Error while checking Current version is up-to-date or newer");
		}
	}

	public static boolean isNewerVersion(String newVersion, String currentVersion) {
		String[] newParts = newVersion.split("\\.");
		String[] currentParts = currentVersion.split("\\.");

		int length = Math.max(newParts.length, currentParts.length);

		for (int i = 0; i < length; i++) {
			int newPart = i < newParts.length ? Integer.parseInt(newParts[i]) : 0;
			int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;

			if (newPart > currentPart) {
				return true;
			} else if (newPart < currentPart) {
				return false;
			}
		}
		return false; // Versions are equal
	}

}
