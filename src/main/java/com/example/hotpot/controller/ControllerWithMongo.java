package com.example.hotpot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/mongo")
public class ControllerWithMongo{

    private static final Logger logger = LoggerFactory.getLogger(HotpotController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

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

        if (code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HotpotResponse("Missing 'code' in request body"));
        }

        // --- Special case for 1102: Read CSV and write text file ---
        if (code.equals("1102")) {
            try {
                processCsvToTextFile("codes.csv", "codes_output.txt");
                logger.info("Successfully processed CSV and wrote to text file");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new HotpotResponse("Hotpot status 200 - CSV processed"));
            } catch (Exception e) {
                logger.error("Error while processing CSV: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new HotpotResponse("Hotpot system 500 - CSV processing error"));
            }
        }

        // --- For all other codes: Check in MongoDB ---
        Query query = new Query(Criteria.where("code").is(code));
        boolean exists = mongoTemplate.exists(query, "codes");

        if (exists) {
            logger.info("Code '{}' found in MongoDB collection 'codes'", code);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new HotpotResponse("Hotpot status 200 - code found"));
        } else {
            logger.info("Code '{}' not found in MongoDB collection 'codes'", code);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HotpotResponse("Hotpot system 404 - code not found"));
        }
    }

    // ----- Helper Method: Read CSV and Write to TXT -----
    private void processCsvToTextFile(String csvFilePath, String outputFilePath) throws IOException {
        // Resolve file paths (assuming CSV is in resources folder)
        Path csvPath = Paths.get("src/main/resources/" + csvFilePath);
        Path txtPath = Paths.get("src/main/resources/" + outputFilePath);

        List<String> lines = Files.readAllLines(csvPath);
        List<String> outputLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                outputLines.add(line);
            }
        }

        Files.write(txtPath, outputLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("Written {} lines from {} to {}", outputLines.size(), csvFilePath, outputFilePath);
    }
}
