package com.dtt.organization.restcontroller;

import com.dtt.common.util.ApiResponse;
import com.dtt.organization.service.iface.EGPVerifyVendorIFace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EGPVerifyVendorController {

    private final EGPVerifyVendorIFace eGPVerifyVendorIFace;

    public EGPVerifyVendorController(EGPVerifyVendorIFace eGPVerifyVendorIFace) {

        this.eGPVerifyVendorIFace = eGPVerifyVendorIFace;
    }
	@GetMapping({"/api/verify-vendor-id"})
    public ApiResponse verifyByEgpForVendor(@RequestParam String vendorId, @RequestParam String orgid) {
        return eGPVerifyVendorIFace.verifyByEgpForVendor(vendorId, orgid);
    }
}
