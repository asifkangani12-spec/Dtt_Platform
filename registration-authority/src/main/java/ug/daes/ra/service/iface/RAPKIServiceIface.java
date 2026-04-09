///*
// * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021,
// * All rights reserved.
// */
//package ug.daes.ra.service.iface;
//
//import ug.daes.ra.request.entity.SetPinModelDto;
//import ug.daes.ra.exception.RAServiceException;
//import ug.daes.ra.request.entity.AuthenticatePKIModel;
//import ug.daes.ra.request.entity.GenerateSignature;
//import ug.daes.ra.request.entity.SetPinModel;
//
//
///**
// * The Interface RAPKIServiceIface.
// */
//public interface RAPKIServiceIface {
//
//	/**
//	 * Sets the pin.
//	 *
//	 * @param setPin the set pin
//	 * @return the string
//	 * @throws RAServiceException the RA service exception
//	 * @throws Exception          the exception
//	 */
//	public String setPin(SetPinModel setPin) throws RAServiceException, Exception;
//
//	/**
//	 * Generate signature.
//	 *
//	 * @param setPin the set pin
//	 * @return the string
//	 * @throws RAServiceException the RA service exception
//	 * @throws Exception          the exception
//	 */
//
//	public String generateSignature(GenerateSignature setPin) throws RAServiceException, Exception;
//
//
//	public String generateSignatureOrganization(GenerateSignature paramGenerateSignature) throws RAServiceException, Exception;
//
//	/**
//	 * Authenticate PKI.
//	 *
//	 * @param generateSignatureRequest the generate signature request
//	 * @return the string
//	 * @throws RAServiceException the RA service exception
//	 * @throws Exception          the exception
//	 */
//	public String authenticatePKI(AuthenticatePKIModel generateSignatureRequest) throws RAServiceException, Exception;
//
//
//
//	public String setPins(SetPinModelDto setPin) throws RAServiceException, Exception;
//}
package ug.daes.ra.service.iface;

import ug.daes.ra.request.entity.SetPinModelDto;
import ug.daes.ra.exception.RAServiceException;
import ug.daes.ra.request.entity.AuthenticatePKIModel;
import ug.daes.ra.request.entity.GenerateSignature;
import ug.daes.ra.request.entity.SetPinModel;

public interface RAPKIServiceIface {

	String setPin(SetPinModel setPin) throws RAServiceException;

	String generateSignature(GenerateSignature generateSignature) throws RAServiceException;

	String generateSignatureOrganization(GenerateSignature generateSignature) throws RAServiceException;

	String authenticatePKI(AuthenticatePKIModel authModel) throws RAServiceException;

	String setPins(SetPinModelDto setPins) throws RAServiceException;
}