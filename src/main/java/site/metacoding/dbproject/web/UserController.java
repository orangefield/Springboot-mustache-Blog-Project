package site.metacoding.dbproject.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import site.metacoding.dbproject.domain.user.User;
import site.metacoding.dbproject.domain.user.UserRepository;

@Controller
public class UserController {

    // 컴포지션(의존성 연결)
    private UserRepository userRepository;

    // DI 받는 코드!!
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 페이지(정적) - 로그인 할 필요 X
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // username=ssar&password=1234&email=ssar@nate.com : x-www-form
    // 회원가입 - 로그인 X
    @PostMapping("/join")
    public String join(User user) {

        System.out.println("user: " + user); // 동기화 되기 전
        User userEntity = userRepository.save(user);
        System.out.println("userEntity: " + userEntity); // 동기화 된 후

        // redirect:매핑주소
        return "redirect:/loginForm"; // 로그인페이지 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지(정적) - 로그인 X
    @GetMapping("/loginForm")
    public String loginForm() {
        // 얘는 파일 찾는거 맞음
        return "user/loginForm";
    }

    // SELECT * FROM user WHERE username = ? AND password = ?
    // 원래 SELECT는 무조건만 GET 요청
    // 그런데 로그인만 예외(POST)
    // 이유 : 주소에 패스워드를 남길 수 없으니까!!(보안을 위해서)
    // 로그인 - 로그인 안한 사람이 할 수 있는거지!
    @PostMapping("/login")
    // User user는 고객에게서 받아온 것(Entity 아님)
    public String login(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();

        // DB에서 동기화되어 받아온것
        User userEntity = userRepository.mLogin(user.getUsername(), user.getPassword());

        if (userEntity == null) {
            System.out.println("아이디 혹은 패스워드가 틀렸습니다");
        } else {
            System.out.println("로그인 되었습니다");
            // 세션 영역에 저장하자
            // 보통 키값을 principal(인증주체), 밸류에 userEntity
            session.setAttribute("principal", userEntity);
        }
        // 1. DB 연결해서 username, password 있는지 확인
        // 2. 있으면 session 영역에 '인증됨' 이라고 메시지 하나 넣어두자

        return "redirect:/"; // PostController 만들고 수정하자
    }

    // 유저상세 페이지(동적 페이지) - 로그인 해야 볼 수 있음 O
    @GetMapping("/user/{id}")
    public String detail(@PathVariable Integer id) {
        return "user/detail";
    }

    // 유저수정 페이지(동적 페이지) - 로그인 O
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "user/updateForm";
    }

    // 유저 수정 - 로그인 O
    @PutMapping("/user/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/user/" + id;
    }

    // 로그아웃 - 로그인 한 사람만 할 수 있지!
    @GetMapping("/logout")
    public String logout() {
        return "메인페이지를 돌려주면 됨"; // PostController 만들고 수정하자
    }
}
