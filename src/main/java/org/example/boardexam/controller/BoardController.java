package org.example.boardexam.controller;

import lombok.RequiredArgsConstructor;
import org.example.boardexam.domain.Board;
import org.example.boardexam.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Board> boards = boardService.findAllBoards(pageable);
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        return "board/list";
    }

    @GetMapping("/view/{id}")
    public String viewBoard(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findBoardById(id));
        return "board/view";
    }

    @GetMapping("/writeform")
    public String addBoard(Model model) {
        model.addAttribute("board", new Board());
        return "board/writeform";
    }

    @PostMapping("/writeform")
    public String addBoard(@ModelAttribute Board board) {
        board.setCreated_at(LocalDateTime.now());
        boardService.saveBoard(board);
        return "redirect:/list";
    }

    @GetMapping("/deleteform/{id}")
    public String deleteBoard(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findBoardById(id));
        return "board/deleteform";
    }

    @PostMapping("/deleteform/{id}")
    public String deleteBoard(@PathVariable Long id, @RequestParam String password,
                              @ModelAttribute Board board, RedirectAttributes redirectAttributes) {
        Board deleteBoard = boardService.findBoardById(id);
        if (deleteBoard.getPassword().equals(password)) {
            boardService.deleteBoardById(id);
            return "redirect:/list";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "redirect:" + id;
        }
    }

    @GetMapping("/updateform/{id}")
    public String updateBoard(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findBoardById(id));
        return "board/updateform";
    }

    @PostMapping("/updateform/{id}")
    public String updateBoard(@PathVariable Long id, @RequestParam String password,
                              @ModelAttribute Board board, RedirectAttributes redirectAttributes) {

        Board updateBoard = boardService.findBoardById(id);

        if (updateBoard.getPassword().equals(password)) {
            updateBoard.setName(board.getName());
            updateBoard.setTitle(board.getTitle());
            updateBoard.setContent(board.getContent());
            updateBoard.setUpdated_at(LocalDateTime.now());

            boardService.saveBoard(updateBoard);

            return "redirect:/view/{id}";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "redirect:" + id;
        }
    }
}
