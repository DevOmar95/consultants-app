package com.example.consultants_api;

import com.example.consultants_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "ThisIsAVeryLongSecretKeyForTestingPurposesOnly1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void generate_createsValidToken() {
        String token = jwtUtil.generate("testuser");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generate("testuser");

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void isValid_withValidToken_returnsTrue() {
        String token = jwtUtil.generate("testuser");

        assertTrue(jwtUtil.isValid(token));
    }

    @Test
    void isValid_withInvalidToken_returnsFalse() {
        assertFalse(jwtUtil.isValid("invalid.token.here"));
    }

    @Test
    void isValid_withEmptyToken_returnsFalse() {
        assertFalse(jwtUtil.isValid(""));
    }

    @Test
    void generate_differentUsers_differentTokens() {
        String token1 = jwtUtil.generate("user1");
        String token2 = jwtUtil.generate("user2");

        assertNotEquals(token1, token2);
    }

    @Test
    void isValid_withExpiredToken_returnsFalse() {
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secret", "ThisIsAVeryLongSecretKeyForTestingPurposesOnly1234567890");
        ReflectionTestUtils.setField(expiredJwtUtil, "expiration", -1000L);

        String token = expiredJwtUtil.generate("testuser");

        assertFalse(jwtUtil.isValid(token));
    }
}
