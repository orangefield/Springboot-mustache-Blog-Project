package site.metacoding.dbproject.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// JPA 라이브러리 : Java(자바언어) Persistence(영구적인 저장) API(노출되어 있는 메서드)
// 자바언어로 DB에 영구적인 저장을 할 수 있게 해주는 메서드를 제공해주는 것
// 1. CRUD 메서드를 기본 제공
// 2. 자바 코드로 DB를 자동 생성 기능 제공
// 3. ORM 제공 - Object Relation Mapping(hibernate 기술)

@AllArgsConstructor
@NoArgsConstructor
@Data // Getter, Setter, toString
@Entity // 서버 실행시 해당 클래스로 테이블을 생성해
@EntityListeners(AuditingEntityListener.class) // 현재시간 입력을 위해 필요한 어노테이션
public class User {
    // IDENTITY 전략은 DB에게 번호증가 전략을 위임하는 것 - 알아서 DB에 맞게 찾아줌
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // pk

    @Column(length = 20, unique = true)
    private String username; // ssar 아이디
    @Column(length = 12, nullable = false)
    private String password;
    @Column(length = 16000000)
    private String email;

    @CreatedDate // insert
    private LocalDateTime createDate;
    @LastModifiedDate // insert, update
    private LocalDateTime updateDate;
}
