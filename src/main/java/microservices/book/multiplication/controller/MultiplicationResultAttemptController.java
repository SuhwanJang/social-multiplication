package microservices.book.multiplication.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservices.book.multiplication.domain.MultiplicationResultAttempt;
import microservices.book.multiplication.service.MultiplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results")
public class MultiplicationResultAttemptController {

  private final MultiplicationService multiplicationService;

  public MultiplicationResultAttemptController(MultiplicationService multiplicationService) {
    this.multiplicationService = multiplicationService;
  }

  @PostMapping
  ResponseEntity<ResultResponse> postResult(
      @RequestBody MultiplicationResultAttempt multiplicationResultAttempt) {
    return ResponseEntity.ok(
        new ResultResponse(multiplicationService.checkAttempt(multiplicationResultAttempt)));
  }

  @RequiredArgsConstructor
  @NoArgsConstructor(force = true)
  @Getter
  static final class ResultResponse {
    private final boolean correct;
  }
}