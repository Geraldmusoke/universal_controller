package com.example.hotpot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/hotpot")
public class HotpotController {

    private static final Logger logger = LoggerFactory.getLogger(HotpotController.class);

    // ----- XML Request Model -----
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

    // ----- XML Response Model -----
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

        try {
            switch (code) {
                case "500":
                    return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Hotpot system 500");

                case "404":
                    return respond(HttpStatus.NOT_FOUND, "Hotpot system 404");

                case "200":
                    return respond(HttpStatus.OK, "Hotpot status 200");

                case "1102":
                    String outputFileName = processCsvToTimestampedText("codes.csv");
                    logger.info("Processed CSV successfully, output file: {}", outputFileName);
                    return respond(HttpStatus.OK,
                            "Hotpot status 200 - CSV processed into " + outputFileName);

                default:
                    return respond(HttpStatus.NOT_FOUND, "Hotpot system 404 - unknown code");
            }
        } catch (Exception e) {
            logger.error("Error handling request for code {}: {}", code, e.getMessage());
            return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Hotpot system 500 - internal error");
        }
    }

    private ResponseEntity<HotpotResponse> respond(HttpStatus status, String message) {
        logger.info("Responding with status: {} ({})", status.value(), message);
        return ResponseEntity.status(status).body(new HotpotResponse(message));
    }

    // ----- Helper: Read CSV and Write to Timestamped TXT -----
    private String processCsvToTimestampedText(String csvFileName) throws IOException {
        Path csvPath = Paths.get("src/main/resources/" + csvFileName);
        if (!Files.exists(csvPath)) {
            throw new IOException("CSV file not found: " + csvFileName);
        }

        // Generate timestamped filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputFileName = "codes_output_" + timestamp + ".txt";
        Path outputPath = Paths.get("src/main/resources/" + outputFileName);

        //Personal

        // Read all non-empty lines from the CSV
        List<String> lines = Files.readAllLines(csvPath);
        List<String> outputLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                outputLines.add(line);
                //Print the lines on the screen
                System.out.println(outputLines);
            }
        }

        // Write to timestamped file
        Files.write(outputPath, outputLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        logger.info("Written {} lines from {} to {}", outputLines.size(), csvFileName, outputFileName);
        return outputFileName;
    }
}
