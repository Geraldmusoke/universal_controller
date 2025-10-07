package com.example.hotpot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@RestController
@RequestMapping("/hotpot")
public class HotpotController {

    private static final Logger logger = LoggerFactory.getLogger(HotpotController.class);

    // ----- XML/JSON Request Model -----
    @XmlRootElement(name = "HotpotRequest")
    public static class HotpotRequest {
        private String code;

        @XmlElement
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // ----- XML/JSON Response Model -----
    @XmlRootElement(name = "HotpotResponse")
    public static class HotpotResponse {
        private String message;

        public HotpotResponse() {}

        public HotpotResponse(String message) {
            this.message = message;
        }

        @XmlElement
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // ----- POST Endpoint -----
    @PostMapping(
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"}
    )
    public ResponseEntity<HotpotResponse> handleHotpotRequest(@RequestBody HotpotRequest request) {
        String code = request.getCode() != null ? request.getCode().trim() : "";
        logger.info("Received Hotpot request with code: {}", code);

        HttpStatus status;
        String message;

        switch (code) {
            case "500":
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Hotpot system 500";
                break;

            case "404":
                status = HttpStatus.NOT_FOUND;
                message = "Hotpot system 404";
                break;

            case "200":
                status = HttpStatus.OK;
                message = "Hotpot status 200";
                break;

            default:
                status = HttpStatus.BAD_REQUEST;
                message = "Invalid code provided";
        }

        logger.info("Responding with status: {} ({})", status.value(), message);
        return ResponseEntity.status(status).body(new HotpotResponse(message));
    }
}
