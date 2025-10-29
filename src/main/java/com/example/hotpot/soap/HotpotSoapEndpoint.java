package com.example.hotpot.soap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.hotpot.soap.QueryDataBalanceRequest;
import com.example.hotpot.soap.QueryDataBalanceResponse;

@Endpoint
public class HotpotSoapEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/hotpot";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "queryDataBalanceRequest")
    @ResponsePayload
    public QueryDataBalanceResponse handleQueryDataBalance(@RequestPayload QueryDataBalanceRequest request) {
        QueryDataBalanceResponse response = new QueryDataBalanceResponse();

        response.setMsisdn(request.getMsisdn());
        response.setSessionId(request.getSessionId());
        response.setChannel(request.getChannel());

        // Add new parameter
        response.setDatacenterLocation("Kampala-EastDC");

        return response;
    }
}
