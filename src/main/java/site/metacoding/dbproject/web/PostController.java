package site.metacoding.dbproject.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class PostController {

    // 글쓰기 페이지 /post/wirteForm - 인증 O
    @GetMapping("/s/post/writeForm")
    public String writeForm() {
        return "/post/writeForm";
    }

    // 이게 홈페이지의 메인이 될거라서 /
    // 글목록 페이지 /, post/list - 인증 X
    @GetMapping({ "/", "post/list" })
    public String list() {
        return "/post/list";
    }

    // 글상세보기 페이지 /post/{id} (삭제버튼, 수정버튼 만들어 두면 됨) - 인증 X
    @GetMapping("/post/{id}") // Get요청에 /post만 제외시킬 것이다(Filter에서)
    public String detail(@PathVariable Integer id) {
        return "/post/detail";
    }

    // 글수정 페이지 /post/{id}/updateForm - 인증 O
    @GetMapping("/s/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "/post/updateForm"; // ViewResolver의 도움 받음(prefix, suffix)
    }

    // DELETE 글삭제 /post/{id} - 글목록으로 가기 - 인증 O
    @DeleteMapping("/s/post/{id}")
    public String delete(@PathVariable Integer id) {
        return "redirect:/";
    }

    // UPDATE 글수정 /post/{id} - 글상세보기 페이지 가기 - 인증 O
    @PutMapping("/s/post/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/post/" + id;
    }

    // POST 글쓰기 /post - 글목록으로 가기 - 인증 O
    @PostMapping("/s/post")
    public String write() {
        return "redirect:/";
    }
}