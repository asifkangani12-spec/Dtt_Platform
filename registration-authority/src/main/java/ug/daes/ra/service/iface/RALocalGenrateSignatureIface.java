package ug.daes.ra.service.iface;

import ug.daes.ra.dto.LogModelDTO;
import ug.daes.ra.exception.RAServiceException;

/**
 * Interface for generating digital signatures locally for Agents.
 */
public interface RALocalGenrateSignatureIface {

	/**
	 * Generates a digital signature for a Subscriber via a local Agent.
	 *
	 * @param logModelDTO The audit log and request data containing the Subscriber UID and hash.
	 * @return The base64 encoded digital signature string.
	 * @throws RAServiceException If the subscriber is not found, certificates are invalid,
	 *                            or the PKI service returns an error.
	 */
	String generateSignatureForAgentSubscriber(LogModelDTO logModelDTO) throws RAServiceException;

	/**
	 * Generates a digital signature for an Organization via a local Agent.
	 *
	 * @param logModelDTO The audit log and request data containing the Organization UID and hash.
	 * @return The base64 encoded digital signature string.
	 * @throws RAServiceException If the organization is not found, certificates are invalid,
	 *                            or the PKI service returns an error.
	 */
	String generateSignatureForAgentOrganization(LogModelDTO logModelDTO) throws RAServiceException;
}