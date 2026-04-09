package ug.daes.ra.service.iface;

import com.dtt.common.util.ApiResponse;


public interface VisitorCompleteDetailsIface {
    ApiResponse getDocumentDetailsBydocumentId(String documentNumber, String documentType);

}
