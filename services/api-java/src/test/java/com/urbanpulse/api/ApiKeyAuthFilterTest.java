package com.urbanpulse.api;

import com.urbanpulse.api.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiKeyAuthFilterTest {

    @Test
    void rejectsMutatingRequestWithoutKeyWhenConfigured() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("secret-demo", true);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/events");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }

    @Test
    void acceptsMutatingRequestWithValidKey() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("secret-demo", true);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/events");
        request.addHeader(ApiKeyAuthFilter.HEADER, "secret-demo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }

    @Test
    void skipsGetEvenWhenRequired() throws Exception {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter("secret-demo", true);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/status");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(200, response.getStatus());
    }
}
