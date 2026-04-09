package ug.daes.onboarding.service.iface;

import com.dtt.common.util.ApiResponse;
import org.springframework.http.HttpHeaders;



public interface ConsentIface {



    ApiResponse signData(HttpHeaders httpHeaders);
}
