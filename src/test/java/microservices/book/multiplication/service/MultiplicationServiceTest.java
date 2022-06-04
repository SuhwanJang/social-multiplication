package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
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
    assertThat(multiplication.getResult()).isEqualTo(1500);
  }
}
