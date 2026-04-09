package ug.daes.OnBoardingTransactionHandler.dto.template;

public class UpdateAgentFcmTokenDto {
	private String suid;

    private String fcmToken;
    
    private String deviceId;

	public String getSuid() {
		return suid;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "UpdateAgentFcmTokenDto [suid=" + suid + ", fcmToken=" + fcmToken + ", deviceId=" + deviceId + "]";
	}
}
