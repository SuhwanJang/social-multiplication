package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  private RandomGeneratorService randomGeneratorService;
  private MultiplicationResultAttemptRepository multiplicationResultAttemptRepository;
  private UserRepository userRepository;

  @Autowired
  public MultiplicationServiceImpl(RandomGeneratorService randomGeneratorService,
                                   MultiplicationResultAttemptRepository attemptRepository,
                                   UserRepository userRepository) {
    this.randomGeneratorService = randomGeneratorService;
    this.multiplicationResultAttemptRepository = attemptRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Multiplication createRandomMultiplication() {
    int factorA = randomGeneratorService.generateRandomFactor();
    int factorB = randomGeneratorService.generateRandomFactor();
    return new Multiplication(factorA, factorB);
  }

  @Override
  public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {
    boolean correct = resultAttempt.getResultAttempt() ==
                        resultAttempt.getMultiplication().getFactorA() *
                        resultAttempt.getMultiplication().getFactorB();
    Assert.isTrue(!resultAttempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");

    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(resultAttempt.getUser(),
            resultAttempt.getMultiplication(), resultAttempt.getResultAttempt(), correct);

    return correct;
  }
}
