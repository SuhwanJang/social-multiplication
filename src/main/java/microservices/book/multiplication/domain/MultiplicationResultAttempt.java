package microservices.book.multiplication.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class MultiplicationResultAttempt {

  private final User user;
  private final Multiplication multiplication;
  private final int resultAttempt;
}
