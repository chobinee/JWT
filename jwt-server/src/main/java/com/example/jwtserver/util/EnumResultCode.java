package com.example.jwtserver.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * resultCode enum
 */
@RequiredArgsConstructor
@Getter
public enum EnumResultCode {
	SUCCESS(0),
	NEED_LOGIN(1),
	EXPIRED_ACC_TOKEN(2),
	EXPIRED_REF_TOKEN(3);

	private final int code;
}
