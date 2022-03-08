package site.metacoding.dbproject.web;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private HttpSession session;
    // private HttpServletRequest request; 절대 하면 안된다! 하나만 띄워서 공유하는 거니까

    // DI 받는 코드!!
    public UserController(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
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
        // 1.1 username, password, email null 체크
        // username=ssar&password=&email=ssar@nate.com 패스워드 공백
        // username=ssar&email=ssar@nate.com 패스워드 null
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "redirect:/joinForm";
        }
        if (user.getUsername().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            return "redirect:/joinForm";
        }
        // 1.2 username, password, email 공백 체크(난 희한한 공백을 체크했구나)
        // if (user.getUsername().contains(" ") || user.getPassword().contains(" ") ||
        // user.getEmail().contains(" ")) {
        // return "redirect:/joinForm";
        // }

        // 2. 핵심 로직
        System.out.println("user: " + user); // 동기화 되기 전
        User userEntity = userRepository.save(user);
        System.out.println("userEntity: " + userEntity); // 동기화 된 후

        // redirect:매핑주소
        return "redirect:/loginForm"; // 로그인페이지 이동해주는 컨트롤러 메서드를 재활용
    }

    // 로그인 페이지(정적) - 로그인 X
    @GetMapping("/loginForm")
    public String loginForm(HttpServletRequest request, Model model) {
        // request.getHeader("Cookie"); -> 파싱해야 함
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();

            for (Cookie cookie : cookies) {
                System.out.println("쿠키값 : " + cookie.getName());
                if (cookie.getName().equals("remember")) {
                    model.addAttribute("remember", cookie.getValue());
                }
                ;
            }
        }
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
    public String login(User user, HttpServletResponse response) {
        // public String login(HttpServletRequest request, User user) {
        // HttpSession session = request.getSession();

        System.out.println("사용자로부터 받은 username, password : " + user);
        // DB에서 동기화되어 받아온것
        User userEntity = userRepository.mLogin(user.getUsername(), user.getPassword());

        if (userEntity == null) {
            System.out.println("아이디 혹은 패스워드가 틀렸습니다");
        } else {
            System.out.println("로그인 되었습니다");
            // 세션 영역에 저장하자
            // 보통 키값을 principal(인증주체), 밸류에 userEntity
            session.setAttribute("principal", userEntity);

            if (user.getRemember() != null && user.getRemember().equals("on")) {
                response.addHeader("Set-Cookie", "remember=" + user.getUsername());
                // response.addHeader(name, value);
            }
        }
        // 1. DB 연결해서 username, password 있는지 확인
        // 2. 있으면 session 영역에 '인증됨' 이라고 메시지 하나 넣어두자

        return "redirect:/"; // PostController 만들고 수정하자
    }

    // 유저상세 페이지(동적 페이지) - 로그인 해야 볼 수 있음 O
    @GetMapping("/user/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        User principal = (User) session.getAttribute("principal");

        // 1. 인증 체크 - 로그인 안했으면 메인으로 로그인페이지로 빠꾸
        if (principal == null) {
            return "error/page1";
        }
        // 2. 권한 체크
        if (principal.getId() != id) {
            return "error/page1";
        }

        // 3. 핵심로직(값이 있는지)
        Optional<User> userOp = userRepository.findById(id);

        if (userOp.isPresent()) {
            User userEntity = userOp.get(); // 있을 때 손 집어넣어라
            model.addAttribute("user", userEntity);
            return "user/detail";
        } else {
            return "error/page1";
        }

        // DB에 로그 남기기 (로그인 한 아이디를)
    }

    // 유저수정 페이지(동적 페이지) - 로그인 O
    @GetMapping("/user/updateForm")
    public String updateForm() {
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
        session.invalidate();
        return "redirect:/loginForm"; // PostController 만들고 수정하자
    }
}