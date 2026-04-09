package ug.daes.onboarding.config;

import io.sentry.Sentry;
import org.springframework.stereotype.Service;

@Service
public class OnboardingSentryClientExceptions {
	

	public void captureExceptions(Throwable e) {
		Sentry.captureException(e);
	}

	public void captureTags(String suid,String mobileNumber ,String methodName, String controller) {
		Sentry.setTag("subscriber_id", suid);
		Sentry.setTag("controller", controller);
		Sentry.setTag("methodName",methodName);
		Sentry.setTag("controller",controller);
	}
}
