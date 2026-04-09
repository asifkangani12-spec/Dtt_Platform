package com.dtt.controller;

import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dtt.requestdto.WalletTransactionDto;
import com.dtt.requestdto.WalletTransactionListDto;
import com.dtt.service.iface.WalletTransactionIface;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class WalletTransactionController {
	

	private final WalletTransactionIface varifyCredentialsIface;

    public WalletTransactionController(WalletTransactionIface varifyCredentialsIface) {
        this.varifyCredentialsIface = varifyCredentialsIface;
    }

    @PostMapping("/api/save-wallet-transaction-log")
	public ApiResponse addWalletTransaction(@RequestBody WalletTransactionDto walletTransactionDto) {
		return varifyCredentialsIface.addWalletTransaction(walletTransactionDto);
	}
	
	@PostMapping("/api/save-list-of-wallet-transaction-log") 
	public ApiResponse addListWalletTransaction(@RequestBody WalletTransactionListDto walletTransactionDto) {
		return varifyCredentialsIface.addListWalletTransaction(walletTransactionDto);
	}
	
	@GetMapping("/wallet-service-status")
	public ApiResponse getServiceStatus() {
		return AppUtil.createApiResponse(true, "Service is running", null);
	}

}

