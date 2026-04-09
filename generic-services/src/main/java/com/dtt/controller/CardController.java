package com.dtt.controller;
import com.dtt.common.util.ApiResponse;
import com.dtt.common.util.AppUtil;
import org.springframework.web.bind.annotation.*;

import com.dtt.service.iface.CardIface;

@RestController
@CrossOrigin(origins = "*")
public class CardController {

	private final CardIface cardIface;

    public CardController(CardIface cardIface) {
        this.cardIface = cardIface;
    }

    @GetMapping("/api/get/card/by/docNum/{idDocNumber}")
	public ApiResponse getPidByIdDocNumber(@PathVariable("idDocNumber") String idDocNumber) {
		try {
			return cardIface.getPidByIdDocNumber(idDocNumber);
		} catch (Exception e) {
			return AppUtil.createApiResponse(false, "Something went wrong", null);
		}
	}

}
