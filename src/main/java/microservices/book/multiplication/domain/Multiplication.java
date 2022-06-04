package microservices.book.multiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Multiplication {

  private final int factorA;
  private final int factorB;

  protected Multiplication() {
    factorA = -1;
    factorB = -1;
  }
}
