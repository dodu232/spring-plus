# SPRING PLUS

## 📊 데이터 생성

* `UUID` 사용 → 닉네임 중복 최소화, 인덱스 효율 ↑

```java
for (int i = 0; i < 1000000; i++) {
    User user = new User(
      "test" + i + "@email.com",
      "123123" + i,
      "유저" + UUID.randomUUID(),
      UserRole.USER
    );
}
```

---

## ⏱️ 성능 측정 방법

* AOP를 활용하여 `@GetMapping` 메서드 기준 응답 속도 측정

```java
@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object logExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object result = pjp.proceed();
        long ms = (System.nanoTime() - start) / 1_000_000;
        log.info("{} executed in {} ms",  pjp.getSignature(), ms);
        return result;
    }
}
```

---

## 📋 인덱스 적용 전

![인덱스 전](https://velog.velcdn.com/images/dodudong/post/9d2adb6e-2563-495c-a702-8b0c85f97c07/image.png)

* **평균 속도: 570ms**

---

## ⚙️ 인덱스 적용

* `nickname` 컬럼에 인덱스 추가

```java
@Table(name = "users", indexes = @Index(name = "idx_users_nickname", columnList = "nickname"))
```

---

## 📋 인덱스 적용 후

![인덱스 후](https://velog.velcdn.com/images/dodudong/post/73a13627-03c3-4be1-be67-e42a43a5d784/image.png)

* **평균 속도: 6.6ms**

---

## 📊 속도 비교

| 구분       | 평균 속도 | 속도 개선          |
| -------- | ----- | -------------- |
| 인덱스 적용 전 | 570ms | baseline       |
| 인덱스 적용 후 | 6.6ms | 86배 개선 (98.8%) |

