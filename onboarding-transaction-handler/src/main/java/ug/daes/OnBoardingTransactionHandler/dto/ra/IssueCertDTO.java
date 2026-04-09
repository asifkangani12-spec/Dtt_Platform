/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.ra;

/**
 * @author Raxit Dubey
 *
 */
public class IssueCertDTO {

	private String subscriberUniqueId;
	
	public String getSubscriberUniqueId() {
		return subscriberUniqueId;
	}

	public void setSubscriberUniqueId(String subscriberUniqueId) {
		this.subscriberUniqueId = subscriberUniqueId;
	}

	@Override
	public String toString() {
		return "IssueCertDTO [subscriberUniqueId=" + subscriberUniqueId + "]";
	}	
	
}
