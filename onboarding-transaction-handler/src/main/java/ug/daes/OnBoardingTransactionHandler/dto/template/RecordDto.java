/**
 * 
 */
package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

/**
 * @author Raxit Dubey
 *
 */
public class RecordDto implements Serializable{

	private String suid;

	public String getSuid() {
		return suid;
	}

	public void setSuid(String suid) {
		this.suid = suid;
	}

	@Override
	public String toString() {
		return "RecordDto [suid=" + suid + "]";
	}
	
}
