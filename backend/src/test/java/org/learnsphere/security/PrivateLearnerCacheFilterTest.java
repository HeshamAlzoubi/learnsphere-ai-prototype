package org.learnsphere.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class PrivateLearnerCacheFilterTest {
    private final PrivateLearnerCacheFilter filter = new PrivateLearnerCacheFilter();

    @Test
    void privateLearnerResponseIsNotCacheable() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/learner/me/attempts");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
    }

    @Test
    void privateAccountResponseIsNotCacheable() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/account/me");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertEquals("no-cache", response.getHeader("Pragma"));
    }

    @Test
    void similarlyNamedPublicRouteIsNotModified() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/accounting-demo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertNull(response.getHeader("Cache-Control"));
    }

    @Test
    void publicDemoEndpointIsNotModified() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/demo-progress");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertNull(response.getHeader("Cache-Control"));
    }
}
