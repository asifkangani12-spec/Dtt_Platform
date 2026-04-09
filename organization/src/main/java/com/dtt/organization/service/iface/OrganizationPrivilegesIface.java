package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;

public interface OrganizationPrivilegesIface {

    ApiResponse getPrivilegesByOrgId(String orgId);

    ApiResponse requestPrivilege(OrganisationPrivilegesRequestDto organisationPrivilegesRequestDto);

    ApiResponse updatePrivilege(UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto);

    ApiResponse getAllPrivileges();

    ApiResponse getOrganizationPrivilegeById(int id);

    ApiResponse getPrivilegesByOrganization(String orgId);

    ApiResponse updateOrganizationPrivilegeList(UpdateOrganizationPrivilegesListDto updateOrganizationPrivilegesListDto);

    ApiResponse getPrivilegesNames();
}
