//package ug.daes.ra.config;
//
//import io.sentry.Sentry;
//import org.springframework.stereotype.Service;
//
//import java.net.UnknownHostException;
//
//@Service
//public class SentryClientExceptions {
//
//
//	public void captureExceptions(Throwable e) {
//		Sentry.captureException(e);
//	}
//
//	public void captureTags(String suid,String mobileNumber ,String methodName, String controller) throws UnknownHostException {
//		Sentry.setTag("subscriber_id", suid);
//		Sentry.setTag("controller", controller);
//		Sentry.setTag("methodName",methodName);
//		Sentry.setTag("mobileNumber",mobileNumber);
//		Sentry.setTag("controller",controller);
//	}
//}
package ug.daes.ra.config;

import io.sentry.Sentry;
import org.springframework.stereotype.Service;

@Service
public class SentryClientExceptions {

	public void captureExceptions(Throwable e) {
		Sentry.captureException(e);
	}

	/**
	 * Safe version: Signature is maintained for Controller compatibility.
	 * Null checks prevent exceptions when 'null' is passed for any parameter.
	 */
	public void captureTags(String suid, String mobileNumber, String methodName, String controller) {

		if (suid != null) {
			Sentry.setTag("subscriber_id", suid);
		}

		if (methodName != null) {
			Sentry.setTag("methodName", methodName);
		}

		if (mobileNumber != null) {
			Sentry.setTag("mobileNumber", mobileNumber);
		}

		if (controller != null) {
			Sentry.setTag("controller", controller);
		}
	}
}