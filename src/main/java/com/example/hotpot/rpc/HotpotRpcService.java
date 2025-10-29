package com.example.hotpot.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotpotRpcService {

    private static final Logger logger = LoggerFactory.getLogger(HotpotRpcService.class);

    /**
     * XML-RPC exposed method.
     * @param msisdn - Mobile number as String
     * @return Data balance in bytes (long)
     */
    public long queryDataBalance(String msisdn) {
        logger.info("XML-RPC request received for MSISDN: {}", msisdn);

        // Mock logic: Generate a random data balance
        long balance = (long) (Math.random() * 10_000_000); // bytes

        logger.info("Returning data balance: {} bytes for MSISDN: {}", balance, msisdn);
        return balance;
    }
}
