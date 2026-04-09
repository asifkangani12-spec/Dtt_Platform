package ug.daes.OnBoardingTransactionHandler.dto.template;

import java.io.Serializable;
import java.util.List;

public class AppConfigurationDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BannersDetailsDto> banners_details;
	private List<CountryDetailsDTO> country_details;
	
	public List<BannersDetailsDto> getBanners_details() {
		return banners_details;
	}
	public void setBanners_details(List<BannersDetailsDto> banners_details) {
		this.banners_details = banners_details;
	}
	
	public List<CountryDetailsDTO> getCountry_details() {
		return country_details;
	}
	public void setCountry_details(List<CountryDetailsDTO> country_details) {
		this.country_details = country_details;
	}
	@Override
	public String toString() {
		return "AppConfigurationDto [banners_details=" + banners_details + ", country_details=" + country_details + "]";
	}

}
