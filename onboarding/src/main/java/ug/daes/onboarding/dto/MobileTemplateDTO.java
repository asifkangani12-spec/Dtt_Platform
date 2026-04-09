package ug.daes.onboarding.dto;

import java.io.Serializable;
import java.util.List;

import ug.daes.onboarding.model.OnbMapMethodOnboardingStep;

public class MobileTemplateDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int templateId;

	private String templateName;

	private String templateMethod;
	
	private String publishedStatus;
	
	private String state;
	
	private List<OnbMapMethodOnboardingStep> steps;

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateMethod() {
		return templateMethod;
	}

	public void setTemplateMethod(String templateMethod) {
		this.templateMethod = templateMethod;
	}

	public String getPublishedStatus() {
		return publishedStatus;
	}

	public void setPublishedStatus(String publishedStatus) {
		this.publishedStatus = publishedStatus;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<OnbMapMethodOnboardingStep> getSteps() {
		return steps;
	}

	public void setSteps(List<OnbMapMethodOnboardingStep> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "MobileTemplateDTO [templateId=" + templateId + ", templateName=" + templateName + ", templateMethod="
				+ templateMethod + ", publishedStatus=" + publishedStatus + ", state=" + state + ", steps=" + steps
				+ "]";
	}


	
}
