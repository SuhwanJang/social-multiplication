package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.event.EventDispatcher;
import microservices.book.multiplication.event.MultiplicationSolvedEvent;
import microservices.book.multiplication.repository.MultiplicationRepository;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MultiplicationServiceTest {

  @Mock
  private RandomGeneratorService randomGeneratorService;

  @Mock
  private MultiplicationResultAttemptRepository attemptRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MultiplicationRepository multiplicationRepository;

  @Mock
  private EventDispatcher eventDispatcher;

  private MultiplicationService multiplicationService;

  @BeforeEach
  public void setUp() {
    multiplicationService = new MultiplicationServiceImpl(
            randomGeneratorService, attemptRepository, userRepository, multiplicationRepository, eventDispatcher);
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
        3000, false);
    MultiplicationResultAttempt verifiedAttempt = new MultiplicationResultAttempt(user, multiplication,
            3000, true);
    MultiplicationSolvedEvent solvedEvent = new MultiplicationSolvedEvent(attempt.getId(),
            attempt.getUser().getId(), true);
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationService.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isTrue();
    verify(attemptRepository).save(verifiedAttempt);
    verify(eventDispatcher).send(eq(solvedEvent));
  }

  @Test
  public void checkWrongAttemptTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication,
        3010, false);
    MultiplicationSolvedEvent solvedEvent = new MultiplicationSolvedEvent(
            attempt.getId(), attempt.getUser().getId(), false
    );
    given(userRepository.findByAlias("John_doe")).willReturn(Optional.empty());

    // when
    boolean attemptResult = multiplicationService.checkAttempt(attempt);

    // assert
    assertThat(attemptResult).isFalse();
    verify(attemptRepository).save(attempt);
    verify(eventDispatcher).send(eq(solvedEvent));
  }

  @Test
  public void retrieveStatsTest() {
    // given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("John_doe");
    List<MultiplicationResultAttempt> attempts = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      attempts.add(new MultiplicationResultAttempt(user, multiplication, 3001 + i, false));
    }
    given(attemptRepository.findTop5ByUserAliasOrderByIdDesc("John_doe")).willReturn(attempts.subList(5, 10));

    // when
    List<MultiplicationResultAttempt> latestAttempts =
            multiplicationService.getStatsForUser("John_doe");
    // then
    assertThat(latestAttempts).isEqualTo(attempts.subList(5, 10));
  }
}
