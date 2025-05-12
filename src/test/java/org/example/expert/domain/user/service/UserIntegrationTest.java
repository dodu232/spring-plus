package org.example.expert.domain.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

//    @Test
    @Rollback(false)
    @DisplayName("대용량 데이터 처리 실습")
    void generateUser() {
        int batchSize = 10_000;
        List<User> batch = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            User user = new User(
                "test" + i + "@email.com",
                "123123" + i,
                "유저" + UUID.randomUUID(),
                UserRole.USER
            );
            batch.add(user);

            if (i % batchSize == 0) {
                userRepository.saveAll(batch);
                userRepository.flush(); // flush로 DB에 강제 반영
                batch.clear();
                System.out.println("저장된 유저 수: " + i);
            }
        }
        if (!batch.isEmpty()) {
            userRepository.saveAll(batch);
            userRepository.flush();
            System.out.println("최종 저장 완료");
        }
    }
}
