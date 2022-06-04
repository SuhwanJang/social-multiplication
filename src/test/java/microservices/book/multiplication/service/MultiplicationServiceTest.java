package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MultiplicationServiceTest {

  @Mock
  private RandomGeneratorService randomGeneratorService;

  private MultiplicationService multiplicationService;

  @BeforeEach
  public void setUp() {
    multiplicationService = new MultiplicationServiceImpl(randomGeneratorService);
  }

  @Test
  public void createRandomMultiplicationTest() {
    // given
    given(randomGeneratorService.generateRandomFactor()).willReturn(50, 30);

    // when
    Multiplication multiplication = multiplicationService.createRandomMultiplication();

    // assert
    assertThat(multiplication.getFactorA()).isEqualTo(50);
    assertThat(multiplication.getFactorB()).isEqualTo(30);
  }

  @Test
  public void checkCorrectAttemptTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication,
        3000);

    // when
    boolean attemptResult = multiplicationService.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isTrue();
  }

  @Test
  public void checkWrongAttemptTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication,
        3010);

    // when
    boolean attemptResult = multiplicationService.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isFalse();
  }
}
