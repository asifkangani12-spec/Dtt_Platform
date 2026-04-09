package com.dtt.organization.service.impl;


import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import com.dtt.common.util.ExceptionHandlerUtil;
import com.dtt.common.util.Utility;
import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;
import com.dtt.organization.model.OrgOrganizationDetails;
import com.dtt.organization.model.OrgOrganizationPrivileges;
import com.dtt.organization.model.OrgSubscriber;
import com.dtt.organization.model.OrgWalletSignCertificate;
import com.dtt.organization.repository.OrganisationPrivilegesRepository;
import com.dtt.organization.repository.OrganizationDetailsRepository;
import com.dtt.organization.repository.OrgSubscriberRepository;
import com.dtt.organization.repository.OrgWalletSignCertRepo;
import com.dtt.organization.response.entity.OrganisationPrivilegesResponse;
import com.dtt.organization.service.iface.OrganizationPrivilegesIface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.dtt.organization.constant.Constant.*;


@Service
public class OrganizationPrivilegesImpl implements OrganizationPrivilegesIface {
    private static final String CLASS = OrganizationServiceImpl.class.getSimpleName();
    private static final Logger logger = LoggerFactory.getLogger(OrganizationPrivilegesImpl.class);

    private  final OrgWalletSignCertRepo walletSignCertRepo;


    private final OrganisationPrivilegesRepository organisationPrivilegesRepository;


    private final OrganizationDetailsRepository organizationDetailsRepository;
    private final ExceptionHandlerUtil exceptionHandlerUtil;



    private final OrgSubscriberRepository subscriberRepository;

    @Value("${privileges}")
    List<String> privileges;




    public OrganizationPrivilegesImpl(OrgWalletSignCertRepo walletSignCertRepo, OrganisationPrivilegesRepository organisationPrivilegesRepository, ExceptionHandlerUtil exceptionHandlerUtil,OrganizationDetailsRepository organizationDetailsRepository,OrgSubscriberRepository subscriberRepository) {
        this.walletSignCertRepo = walletSignCertRepo;
        this.organisationPrivilegesRepository = organisationPrivilegesRepository;
        this.organizationDetailsRepository = organizationDetailsRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;

        this.subscriberRepository=subscriberRepository;
    }


    @Override
    public ApiResponse getPrivilegesByOrgId(String orgId) {
        try {

            if (orgId == null || orgId.trim().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(
                        API_ERROR_ORGANIZATION_ID_REQUIRED);
            }

            OrganisationPrivilegesResponse organisationPrivilegesResponse =
                    new OrganisationPrivilegesResponse();

            OrgOrganizationDetails organizationDetails =
                    organizationDetailsRepository.findByOrganizationUid(orgId);

            if (organizationDetails == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        API_ERROR_ORGANIZATION_NOT_FOUND);
            }

            OrgWalletSignCertificate walletSignCertificate =
                    walletSignCertRepo.findByOrganizationId("ACTIVE", orgId);

            organisationPrivilegesResponse
                    .setWalletCertificateStatus(walletSignCertificate != null);

            organisationPrivilegesResponse.setPrivileges(
                    organisationPrivilegesRepository
                            .fetchPrivilegesByOrganisation(orgId));

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.data.retrieved",
                    organisationPrivilegesResponse);

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ID: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    private ApiResponse validateRequest(OrganisationPrivilegesRequestDto dto) {

        if (isEmpty(dto.getOrganizationId())) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_ORGANIZATION_ID_REQUIRED);
        }

        if (organizationDetailsRepository.findByOrganizationUid(dto.getOrganizationId()) == null) {
            return exceptionHandlerUtil.createErrorResponse(API_ERROR_ORGANIZATION_NOT_FOUND);
        }

        if (isEmpty(dto.getSuid())) {
            return exceptionHandlerUtil.createErrorResponse("api.error.spoc.id.required");
        }

        if (subscriberRepository.getSubscriberEmail(dto.getSuid()) == null) {
            return exceptionHandlerUtil.createErrorResponse("api.error.subscriber.not.found");
        }

        if (dto.getPrivileges() == null || dto.getPrivileges().isEmpty()) {
            return exceptionHandlerUtil.createErrorResponse("api.error.privileges.required");
        }

        return null; // No errors
    }

    @Override
    public ApiResponse requestPrivilege(OrganisationPrivilegesRequestDto dto) {
        try {

            ApiResponse validationError = validateRequest(dto);
            if (validationError != null) return validationError;

            OrgOrganizationDetails org = organizationDetailsRepository
                    .findByOrganizationUid(dto.getOrganizationId());

            OrgSubscriber subscriber = subscriberRepository.getSubscriberEmail(dto.getSuid());

            List<OrgOrganizationPrivileges> requestedPrivileges = buildPrivileges(dto, org, subscriber);

            List<OrgOrganizationPrivileges> saved = organisationPrivilegesRepository.saveAll(requestedPrivileges);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.organization.privileges.requested", saved);

        } catch (Exception e) {
            logger.error("{} - {} : Exception occurred during organization Privilege REQUEST: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    private List<OrgOrganizationPrivileges> buildPrivileges(
            OrganisationPrivilegesRequestDto dto,
            OrgOrganizationDetails org,
            OrgSubscriber subscriber) {

        List<OrgOrganizationPrivileges> list = new ArrayList<>();

        for (String privilege : dto.getPrivileges()) {

            OrgOrganizationPrivileges existing = organisationPrivilegesRepository.fetchByPrivilege(
                    dto.getOrganizationId(), privilege);

            if (existing != null && !REJECTED.equals(existing.getStatus())) {
                throw new IllegalArgumentException("api.error.privilege.already.exists");
            }

            list.add(createPrivilege(privilege, org, subscriber));
        }

        return list;
    }

    private OrgOrganizationPrivileges createPrivilege(
            String privilege,
            OrgOrganizationDetails org,
            OrgSubscriber subscriber) {

        OrgOrganizationPrivileges p = new OrgOrganizationPrivileges();
        p.setPrivilege(privilege);
        p.setOrganizationName(org.getOrganizationName());
        p.setOrganizationId(org.getOrganizationUid());
        p.setCreatedBy(subscriber.getFullName());
        p.setCreatedOn(AppUtil.getDate());
        p.setStatus("APPLIED");
        return p;
    }
    @Override
    public ApiResponse updatePrivilege(UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto) {
        try {

            if (updateOrganizationPrivilegeDto.getId() <= 0) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.id.required");
            }

            if (updateOrganizationPrivilegeDto.getStatus() == null
                    || updateOrganizationPrivilegeDto.getStatus().trim().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.status.required");
            }

            OrgOrganizationPrivileges organizationPrivileges =
                    organisationPrivilegesRepository
                            .fetchById(updateOrganizationPrivilegeDto.getId());

            if (organizationPrivileges == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.not.found");
            }

            String currentStatus = organizationPrivileges.getStatus();
            String newStatus = updateOrganizationPrivilegeDto.getStatus();

            if (REJECTED.equals(currentStatus)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.update.rejected");
            }

            if (APPROVED.equals(currentStatus)
                    && REJECTED.equals(newStatus)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.reject.approved.not.allowed");
            }

            if ("APPLIED".equals(currentStatus)
                    && "SUSPENDED".equals(newStatus)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.suspend.applied.not.allowed");
            }

            if ("SUSPENDED".equals(currentStatus)
                    && "REJECTED".equals(newStatus)) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.reject.suspended.not.allowed");
            }

            organizationPrivileges.setStatus(newStatus);
            organizationPrivileges.setModifiedBy(
                    updateOrganizationPrivilegeDto.getAdminName());
            organizationPrivileges.setModifiedOn(AppUtil.getDate());

            OrgOrganizationPrivileges updatedPrivilege =
                    organisationPrivilegesRepository.save(organizationPrivileges);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.privilege.updated",
                    updatedPrivilege);

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege UPDATE: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ApiResponse getAllPrivileges() {
        try {

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.records.fetched",
                    organisationPrivilegesRepository.getAll());

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege GET ALL: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ApiResponse getOrganizationPrivilegeById(int id) {
        try {

            if (id <= 0) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.id.invalid");
            }

            OrgOrganizationPrivileges organizationPrivileges =
                    organisationPrivilegesRepository.fetchById(id);

            if (organizationPrivileges == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privilege.not.found");
            }

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.record.fetched",
                    organizationPrivileges);

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ID: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ApiResponse getPrivilegesByOrganization(String orgId) {
        try {

            if (orgId == null || orgId.isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.organization.id.required");
            }

            OrgOrganizationDetails organizationDetails =
                    organizationDetailsRepository.findByOrganizationUid(orgId);

            if (organizationDetails == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.organization.not.found");
            }

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.records.fetched",
                    organisationPrivilegesRepository.fetchPrivilegesByOrganisation(orgId));

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ORGANIZATION: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ApiResponse updateOrganizationPrivilegeList(
            UpdateOrganizationPrivilegesListDto dto) {

        try {

            if (dto.getOrgId() == null || dto.getOrgId().isEmpty()) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.organization.id.required");
            }

            OrgOrganizationDetails organizationDetails =
                    organizationDetailsRepository.findByOrganizationUid(dto.getOrgId());

            if (organizationDetails == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.organization.not.found");
            }

            if (dto.getPrivileges() == null) {
                return exceptionHandlerUtil.createErrorResponse(
                        "api.error.privileges.required");
            }

            // Reset existing privileges
            organisationPrivilegesRepository.updatePrivileges(dto.getOrgId());

            if (dto.getPrivileges().isEmpty()) {
                return exceptionHandlerUtil.createSuccessResponse(
                        "api.response.privileges.updated",
                        null);
            }

            List<OrgOrganizationPrivileges> organizationPrivilegesList = new ArrayList<>();

            for (String privilege : dto.getPrivileges()) {

                OrgOrganizationPrivileges organizationPrivileges =
                        organisationPrivilegesRepository.fetchByPrivilege(dto.getOrgId(), privilege);

                if (organizationPrivileges == null) {

                    OrgOrganizationPrivileges newPrivilege = new OrgOrganizationPrivileges();
                    newPrivilege.setOrganizationId(dto.getOrgId());
                    newPrivilege.setOrganizationName(organizationDetails.getOrganizationName());
                    newPrivilege.setPrivilege(privilege);
                    newPrivilege.setStatus("APPROVED");
                    newPrivilege.setCreatedBy(dto.getModifiedBy());
                    newPrivilege.setCreatedOn(AppUtil.getDate());

                    organizationPrivilegesList.add(newPrivilege);

                } else {

                    organizationPrivileges.setStatus("APPROVED");
                    organizationPrivileges.setModifiedBy(dto.getModifiedBy());
                    organizationPrivileges.setModifiedOn(AppUtil.getDate());

                    organizationPrivilegesList.add(organizationPrivileges);
                }
            }

            List<OrgOrganizationPrivileges> savedList =
                    organisationPrivilegesRepository.saveAll(organizationPrivilegesList);

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.privileges.updated",
                    savedList);

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege UPDATE ORG PRIVILEGE LIST: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

    @Override
    public ApiResponse getPrivilegesNames() {
        try {

            return exceptionHandlerUtil.createSuccessResponse(
                    "api.response.privileges.fetched",
                    privileges);

        } catch (Exception e) {

            logger.error("{} - {} : Exception occurred during organization Privilege GET NAMES: {}",
                    CLASS, Utility.getMethodName(), e.getMessage());
            return exceptionHandlerUtil.handleException(e);
        }
    }

}
