package microservices.book.multiplication.domain;


import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name="USERS")
public final class User {

  @Id
  @GeneratedValue
  @Column(name = "USER_ID")
  private Long id;

  private final String alias;
}
