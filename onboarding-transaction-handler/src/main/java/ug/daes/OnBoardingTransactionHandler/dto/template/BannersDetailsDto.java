package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;

public class BannersDetailsDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String banner_text;
	private String banner_Small_Image;
	private String bannner_Background_Image;
	private String bannner_Full_Image;
	
	public String getBanner_text() {
		return banner_text;
	}
	public void setBanner_text(String banner_text) {
		this.banner_text = banner_text;
	}
	public String getBanner_Small_Image() {
		return banner_Small_Image;
	}
	public void setBanner_Small_Image(String banner_Small_Image) {
		this.banner_Small_Image = banner_Small_Image;
	}
	public String getBannner_Background_Image() {
		return bannner_Background_Image;
	}
	public void setBannner_Background_Image(String bannner_Background_Image) {
		this.bannner_Background_Image = bannner_Background_Image;
	}
	public String getBannner_Full_Image() {
		return bannner_Full_Image;
	}
	public void setBannner_Full_Image(String bannner_Full_Image) {
		this.bannner_Full_Image = bannner_Full_Image;
	}
	@Override
	public String toString() {
		return "BannersDetailsDto [banner_text=" + banner_text + ", banner_Small_Image=" + banner_Small_Image
				+ ", bannner_Background_Image=" + bannner_Background_Image + ", bannner_Full_Image="
				+ bannner_Full_Image + "]";
	}
	
}
