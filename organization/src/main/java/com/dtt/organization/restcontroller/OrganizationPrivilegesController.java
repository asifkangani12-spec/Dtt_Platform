package com.dtt.organization.restcontroller;


import com.dtt.common.util.ApiResponse;
import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;
import com.dtt.organization.service.iface.OrganizationPrivilegesIface;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrganizationPrivilegesController {





    private final OrganizationPrivilegesIface organizationPrivilegesIface;

    public OrganizationPrivilegesController( OrganizationPrivilegesIface organizationPrivilegesIface) {

        this.organizationPrivilegesIface = organizationPrivilegesIface;
    }

@GetMapping("/api/get/privilege/by/orgId/{orgId}")
    public ApiResponse getPrivilegesByOrgId(@PathVariable String orgId){
    return organizationPrivilegesIface.getPrivilegesByOrgId(orgId);
}



@PostMapping("/api/request/organization/privilege")
    public ApiResponse requestPrivilege(@RequestBody OrganisationPrivilegesRequestDto organisationPrivilegesRequestDto){

    return organizationPrivilegesIface.requestPrivilege(organisationPrivilegesRequestDto);
    }


    @PostMapping("/api/update/organization/privilege")
    public ApiResponse updatePrivilege(@RequestBody UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto){
        return organizationPrivilegesIface.updatePrivilege(updateOrganizationPrivilegeDto);
    }

    @GetMapping("/api/get/all/privileges")
    public ApiResponse getAllPrivileges(){
        return organizationPrivilegesIface.getAllPrivileges();
    }

    @GetMapping("/api/get/privilege/by/id/{id}")
    public ApiResponse getPrivilegeById(@PathVariable("id") int id){
        return organizationPrivilegesIface.getOrganizationPrivilegeById(id);
    }

    @GetMapping("/api/get/privileges/by/organization/{orgId}")
    public ApiResponse getPrivilegesByOrganization(@PathVariable("orgId") String orgId){
        return organizationPrivilegesIface.getPrivilegesByOrganization(orgId);
    }


    @PostMapping("/api/update/organization/privileges")
    public ApiResponse updateOrganizationPrivilegeList(@RequestBody UpdateOrganizationPrivilegesListDto updateOrganizationPrivilegesListDto){
        return organizationPrivilegesIface.updateOrganizationPrivilegeList(updateOrganizationPrivilegesListDto);

    }

    @GetMapping("/api/get/privileges")
    public ApiResponse getPrivilegesNames(){
        return organizationPrivilegesIface.getPrivilegesNames();
    }








}
