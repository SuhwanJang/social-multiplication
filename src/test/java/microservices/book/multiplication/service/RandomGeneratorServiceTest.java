package microservices.book.multiplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomGeneratorServiceTest {

  private RandomGeneratorService randomGeneratorService;

  @BeforeEach
  public void setUp() {
    randomGeneratorService = new RandomGeneratorServiceImpl();
  }

  @Test
  public void generateRandomFactorIsBetweenExpectedLimits() throws Exception {
    List<Integer> randomFactors = IntStream.range(0, 1000)
        .map(i -> randomGeneratorService.generateRandomFactor())
        .boxed()
        .collect(Collectors.toList());
    assertThat(randomFactors)
        .containsOnlyOnceElementsOf(IntStream.range(11, 100)
            .boxed()
            .collect(Collectors.toList()));
  }
}
