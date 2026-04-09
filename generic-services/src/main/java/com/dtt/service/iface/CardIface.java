package com.dtt.service.iface;


import com.dtt.common.util.ApiResponse;

public interface CardIface {

	ApiResponse getPidByIdDocNumber(String idDocNumber);

}
