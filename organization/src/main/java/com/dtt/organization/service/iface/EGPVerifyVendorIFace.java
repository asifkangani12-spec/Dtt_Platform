package com.dtt.organization.service.iface;

import com.dtt.common.util.ApiResponse;

public interface EGPVerifyVendorIFace {
	ApiResponse verifyByEgpForVendor(String vendorId, String orgId);
}
