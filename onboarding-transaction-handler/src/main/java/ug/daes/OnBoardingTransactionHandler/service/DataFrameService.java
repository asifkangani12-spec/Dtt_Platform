package ug.daes.OnBoardingTransactionHandler.service;

import java.lang.reflect.Method;
import java.util.Locale;


import com.dtt.common.config.PlatformHttpConfig;
import com.dtt.common.constants.ErrorCode;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import ug.daes.OnBoardingTransactionHandler.constant.MethodConstant;
import ug.daes.OnBoardingTransactionHandler.dto.DataFrameGetRequest;
import ug.daes.OnBoardingTransactionHandler.dto.DataFramePostRequest;


import org.springframework.context.i18n.LocaleContextHolder;


@Service
public class DataFrameService {

    private final PlatformHttpConfig platformHttpConfig;

	private final MessageSource messageSource;

	private final ApplicationContext context;

	public DataFrameService(PlatformHttpConfig platformHttpConfig, MessageSource messageSource, ApplicationContext context) {
		this.platformHttpConfig = platformHttpConfig;
		this.messageSource = messageSource;
		this.context = context;
	}

	public ApiResponse ResponseServ(DataFramePostRequest dataFrame) throws Exception {

		String method = dataFrame.getServiceMethod();
		ApiResponse res = lookUpService(method);

		if (!res.isSuccess()) {
			return res;
		} else {
			if (res.getMessage().equals("Asynchronous")) {
				return asynchronous(dataFrame);
			}
			if (res.getMessage().equals("Synchronous")) {
				return synchronous(dataFrame);
			}
		}
		return null;
	}

	public ApiResponse ResponseServ(HttpHeaders httpHeaders,DataFramePostRequest dataFrame) throws Exception {

		String method = dataFrame.getServiceMethod();
		ApiResponse res = lookUpService(method);

		if (!res.isSuccess()) {
			return res;
		} else {
			if (res.getMessage().equals("Asynchronous")) {
				return asynchronous(dataFrame);
			}
			if (res.getMessage().equals("Synchronous")) {
				return synchronous(httpHeaders,dataFrame);
			}
		}
		return null;
	}
	public ApiResponse ResponseServByID(HttpHeaders httpHeaders,DataFramePostRequest dataFrame, int id) throws Exception {

		String method = dataFrame.getServiceMethod();
		ApiResponse res = lookUpService(method);

		if (!res.isSuccess()) {
			return res;
		} else {
			if (res.getMessage().equals("AsynchronousToken") || res.getMessage().equals("SynchronousToken")) {

				if (res.getMessage().equals("AsynchronousToken")) {
					return asynchronous(dataFrame);
				}
				if (res.getMessage().equals("SynchronousToken")) {
					return synchronousById(httpHeaders,dataFrame, id);
				}
			}

			if (res.getMessage().equals("Asynchronous")) {
				return asynchronous(dataFrame);
			}
			if (res.getMessage().equals("Synchronous")) {
				return synchronousById(httpHeaders,dataFrame, id);
			}
		}
		return null;
	}

	public ApiResponse lookUpService(String method) {

		if (!(MethodConstant.getMap().containsKey(method))) {

			return new ApiResponse(
					false,
					messageSource.getMessage(
							"api.error.service.not.found",
							null,
							LocaleContextHolder.getLocale()
					),
					null
			);
		} else {

			if (MethodConstant.getMap().get(method).getNature().equals("Asynchronous")) {
				if (MethodConstant.getMap().get(method).isAuth()) {
					return new ApiResponse(true, "AsynchronousToken", MethodConstant.getMap().get(method).getActivity());
				}
				return new ApiResponse(true, "Asynchronous", MethodConstant.getMap().get(method).getActivity());
			} else {
				if (MethodConstant.getMap().get(method).isAuth()) {
					return new ApiResponse(true, "SynchronousToken", MethodConstant.getMap().get(method).getActivity());
				}
				return new ApiResponse(true, "Synchronous", MethodConstant.getMap().get(method).getActivity());
			}
		}

	}

	public ApiResponse asynchronous(DataFramePostRequest dataFrame) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse r = null;
		ResponseEntity<ApiResponse> res = null;
		try {
			String cname = MethodConstant.getMap().get(method).getClassName();
			System.out.println(cname);

			Object o = context.getBean(cname);
			if (MethodConstant.getMap().get(method).getPar() > 0) {
				Class[] arg = new Class[MethodConstant.getMap().get(method).getPar()];
				arg[0] = Object.class;

				Method method1 = o.getClass().getMethod(method, arg);
				r = (ApiResponse) method1.invoke(o, dataFrame.getRequestBody());
			} else {
				Method method1 = o.getClass().getMethod(method);
				r = (ApiResponse) method1.invoke(o);
			}

			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			String url = "http://ekyc.digitaltrusttech.com:90/ticket-service/api/ticket/generate";
			res = rest.exchange(url, HttpMethod.POST, requestEntity, ApiResponse.class);
			if (res.getStatusCodeValue() == 400 || res.getStatusCodeValue() == 401 || res.getStatusCodeValue() == 403 || res.getStatusCodeValue() == 404 || res.getStatusCodeValue() == 415 || res.getStatusCodeValue() == 500 || res.getStatusCodeValue() == 501 || res.getStatusCodeValue() == 503) {
				return AppUtil.createApiResponse(
						false,
						messageSource.getMessage(
								"api.error.generic",
								null,
								Locale.getDefault()
						),
						null
				);
			}
			if (res.getStatusCodeValue() == 200 || res.getStatusCodeValue() == 201) {
				return res.getBody();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			final String errCode = ErrorCode.getOrDefault("Illegal Argument Exception_AppConfig-service","104120");

			return AppUtil.createApiResponse(
					false,
					messageSource.getMessage(
							"api.error.service.not.found.code",
							new Object[]{errCode},
							Locale.getDefault()
					),
					null
			);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			final String errCode = ErrorCode.getOrDefault("Illegal State Exception_AppConfig-service","104115");
			return AppUtil.createApiResponse(
					false,
					messageSource.getMessage(
							"api.error.service.not.found.code",
							new Object[]{errCode},
							Locale.getDefault()
					),
					null
			);
		} catch (NullPointerException e) {
			e.printStackTrace();
			final String errCode = ErrorCode.getOrDefault("Null pointer Exception_AppConfig-service","104118");
			return AppUtil.createApiResponse(
					false,
					messageSource.getMessage(
							"api.error.service.not.found.code",
							new Object[]{errCode},
							Locale.getDefault()
					),
					null
			);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			e.printStackTrace();
			final String errCode = ErrorCode.getOrDefault("Not Found","104044");
			return AppUtil.createApiResponse(
					false,
					messageSource.getMessage(
							"api.error.service.not.found.code",
							new Object[]{errCode},
							Locale.getDefault()
					),
					null
			);
		} catch (Exception e) {

			e.printStackTrace();
			final String errCode = ErrorCode.getOrDefault("EXCEPTION_AppConfigService","104120");
			return AppUtil.createApiResponse(false, "Something went wrong.Please try after (" + errCode + ")",
					(Object) null);
		}

		return AppUtil.createApiResponse(
				false,
				messageSource.getMessage(
						"api.error.server.unreachable",
						null,
						Locale.getDefault()
				),
				null
		);
	}

	public ApiResponse synchronous(DataFramePostRequest dataFrame) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse r = null;
		try {

			String cname = MethodConstant.getMap().get(method).getClassName();

			Object o = context.getBean(cname);

			if (MethodConstant.getMap().get(method).getPar() > 0) {
				Class[] arg = new Class[MethodConstant.getMap().get(method).getPar()];
				System.out.println("arg 0: "+arg[0]);
				arg[0] = Object.class;

				Method method1 = o.getClass().getMethod(method, arg);
				r = (ApiResponse) method1.invoke(o, dataFrame.getRequestBody());
				return r;
			} else {
				System.out.println("method ==>> " + method);
				Method method1 = o.getClass().getMethod(method);
				r = (ApiResponse) method1.invoke(o, (Object[]) null);
				return r;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(e.getCause());
			throw new Exception(e);
		}
	}

	public ApiResponse synchronous(HttpHeaders httpHeaders,DataFramePostRequest dataFrame) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse r = null;
		try {

			String cname = MethodConstant.getMap().get(method).getClassName();
			Class cl = Class.forName("ug.daes.OnBoardingTransactionHandler.service." + cname);

			Object o = context.getBean(cname);
			System.out.println(cl);
			if (MethodConstant.getMap().get(method).getPar() > 0) {

				Class[] arg = new Class[MethodConstant.getMap().get(method).getPar()];
				arg[0] = Object.class;

				arg = new Class<?>[2];
				arg[0] = HttpHeaders.class;
				arg[1] = Object.class;
				Method method1 = o.getClass().getMethod(method, arg);
				r = (ApiResponse) method1.invoke(o, httpHeaders, dataFrame.getRequestBody());
				return r;
			} else {
				System.out.println("method ==>> " + method);
				Method method1 = o.getClass().getMethod(method);
				r = (ApiResponse) method1.invoke(o, (Object[]) null);
				return r;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(e.getCause());
			throw new Exception(e);
		}
	}

	public ApiResponse synchronousById(HttpHeaders httpHeaders,DataFramePostRequest dataFrame, int id) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse r = null;
		try {

			String cname = MethodConstant.getMap().get(method).getClassName();
			Object o = context.getBean(cname);
			if (MethodConstant.getMap().get(method).getPar() > 0) {
				Class[] arg = new Class[MethodConstant.getMap().get(method).getPar()];
				arg[0] = Object.class;

				arg = new Class<?>[2];
				arg[0] = HttpHeaders.class;
				arg[1] = Object.class;
				Method method1 = o.getClass().getMethod(method, arg);
				r = (ApiResponse) method1.invoke(o,httpHeaders, id);
				return r;
			} else {
				System.out.println("method ==>> " + method + "(" + id + ")");
				Method method1 = o.getClass().getMethod(method);
				r = (ApiResponse) method1.invoke(o, (Object[]) null);
				return r;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(e.getCause());
			throw new Exception(e);
		}
	}

	public ApiResponse dataGetReq(DataFrameGetRequest dataFrame) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse res = lookUpService(method);
		if (!res.isSuccess()) {
			return res;
		} else {
            return asynchrGet(dataFrame);
        }
	}

	public ApiResponse asynchrGet(DataFrameGetRequest dataFrame) throws Exception {
		String method = dataFrame.getServiceMethod();
		ApiResponse r = null;
		try {
			String cname = MethodConstant.getMap().get(method).getClassName();
			Object o = context.getBean(cname);
			if (MethodConstant.getMap().get(method).getPar() > 0) {
				Class[] arg = new Class[MethodConstant.getMap().get(method).getPar()];
				arg[0] = Object.class;

				Method method1 = o.getClass().getMethod(method, arg);
				System.out.println("dataFrame.getPars()" + dataFrame.getPars());
				r = (ApiResponse) method1.invoke(o, dataFrame.getPars());
				return r;
			} else {
				Method method1 = o.getClass().getMethod(method);
				r = (ApiResponse) method1.invoke(o, (Object[]) null);

				return r;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
