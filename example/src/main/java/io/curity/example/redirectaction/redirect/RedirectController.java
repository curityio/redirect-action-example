/*
 *  Copyright 2020 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.curity.example.redirectaction.redirect;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public final class RedirectController
{
    private final Map<String, String> cache = new HashMap<>();

    @GetMapping("/redirect")
    @ResponseStatus(HttpStatus.FOUND)
    public void redirect(@RequestParam String status, @RequestParam String resumeUrl, HttpServletResponse response) {
        cache.put(status, UUID.randomUUID().toString());
        response.setHeader("Location", resumeUrl);
    }

    @GetMapping("/getdata")
    public ResponseData getData(@RequestParam String status) {
        String userId = cache.get(status);

        if (userId == null) {
            throw new NotFoundException();
        }

        return new ResponseData(userId, "ACTIVE");
    }

    private final static class ResponseData {
        private final String externalUserId;
        private final String externalStatus;

        public ResponseData(String externalUserId, String externalStatus)
        {
            this.externalUserId = externalUserId;
            this.externalStatus = externalStatus;
        }

        public String getExternalUserId()
        {
            return externalUserId;
        }

        public String getExternalStatus()
        {
            return externalStatus;
        }
    }

    private final static class NotFoundException extends RuntimeException {}

    @ControllerAdvice
    private final static class ExceptionController {

        @ExceptionHandler(NotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public void handleNotFound() {}
    }
}
