package com.dtt.service.iface;

import com.dtt.common.util.ApiResponse;
import com.dtt.requestdto.WalletTransactionDto;
import com.dtt.requestdto.WalletTransactionListDto;

public interface WalletTransactionIface {
	
ApiResponse addWalletTransaction(WalletTransactionDto walletTransactionDto);
	
	ApiResponse addListWalletTransaction(WalletTransactionListDto walletTransactionDto);

}
