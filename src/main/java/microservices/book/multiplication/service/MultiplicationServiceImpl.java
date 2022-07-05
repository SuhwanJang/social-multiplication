package microservices.book.multiplication.service;

import microservices.book.multiplication.domain.Multiplication;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.domain.User;
import microservices.book.multiplication.event.EventDispatcher;
import microservices.book.multiplication.event.MultiplicationSolvedEvent;
import microservices.book.multiplication.repository.MultiplicationRepository;
import microservices.book.multiplication.repository.MultiplicationResultAttemptRepository;
import microservices.book.multiplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplicationServiceImpl implements MultiplicationService {

  private RandomGeneratorService randomGeneratorService;
  private MultiplicationResultAttemptRepository attemptRepository;
  private MultiplicationRepository multiplicationRepository;
  private UserRepository userRepository;
  private EventDispatcher eventDispatcher;

  @Autowired
  public MultiplicationServiceImpl(final RandomGeneratorService randomGeneratorService,
                                   final MultiplicationResultAttemptRepository attemptRepository,
                                   final UserRepository userRepository,
                                   final MultiplicationRepository multiplicationRepository,
                                   final EventDispatcher eventDispatcher) {
    this.randomGeneratorService = randomGeneratorService;
    this.attemptRepository = attemptRepository;
    this.userRepository = userRepository;
    this.multiplicationRepository = multiplicationRepository;
    this.eventDispatcher = eventDispatcher;
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
    Optional<Multiplication> multiplication = multiplicationRepository.findByFactorAAndFactorB(
            resultAttempt.getMultiplication().getFactorA(), resultAttempt.getMultiplication().getFactorB()
    );
    boolean correct = resultAttempt.getResultAttempt() ==
                        resultAttempt.getMultiplication().getFactorA() *
                        resultAttempt.getMultiplication().getFactorB();
    Assert.isTrue(!resultAttempt.isCorrect(), "채점한 상태로 보낼 수 없습니다.");

    MultiplicationResultAttempt checkedAttempt = new MultiplicationResultAttempt(user.orElse(resultAttempt.getUser()),
            multiplication.orElse(resultAttempt.getMultiplication()), resultAttempt.getResultAttempt(), correct);

    attemptRepository.save(checkedAttempt);

    eventDispatcher.send(new MultiplicationSolvedEvent(
            checkedAttempt.getId(),
            checkedAttempt.getUser().getId(),
            checkedAttempt.isCorrect())
    );

    return correct;
  }

  @Override
  public List<MultiplicationResultAttempt> getStatsForUser(String userAlias) {
    return attemptRepository.findTop5ByUserAliasOrderByIdDesc(userAlias);
  }
}
