package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  private RandomGeneratorService randomGeneratorService;
  private MultiplicationResultAttemptRepository attemptRepository;
  private UserRepository userRepository;

  @Autowired
  public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService,
                                   final MultiplicationResultAttemptRepository attemptRepository,
                                   final UserRepository userRepository) {
    this.randomGeneratorService = randomGeneratorService;
    this.attemptRepository = attemptRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Multiplication createRandomMultiplication() {
    int factorA = randomGeneratorService.generateRandomFactor();
    int factorB = randomGeneratorService.generateRandomFactor();
    return new Multiplication(factorA, factorB);
  }

  @Transactional
  @Override
  public boolean checkAttempt(MultiplicationResultAttempt resultAttempt) {
    Optional<User> user = userRepository.findByAlias(resultAttempt.getUser().getAlias());
    boolean correct = resultAttempt.getResultAttempt() ==
                        resultAttempt.getMultiplication().getFactorA() *
                        resultAttempt.getMultiplication().getFactorB();
    Assert.isTrue(!resultAttempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");

    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(resultAttempt.getUser(),
            resultAttempt.getMultiplication(), resultAttempt.getResultAttempt(), correct);

    attemptRepository.save(checkedAttempt);

    return correct;
  }
}
