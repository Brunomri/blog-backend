package com.bmri.blogbackend;

import com.bmri.blogbackend.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BlogBackendApplicationTests {

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void contextLoads() {
    }

}
