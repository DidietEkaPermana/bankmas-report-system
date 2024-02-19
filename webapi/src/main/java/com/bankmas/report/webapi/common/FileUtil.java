package com.bankmas.report.webapi.common;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

	public static String calculateChecksum(byte[] data) throws IOException, NoSuchAlgorithmException {
		byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
		String checksum = new BigInteger(1, hash).toString(16);
		
		return checksum;
    }
}
