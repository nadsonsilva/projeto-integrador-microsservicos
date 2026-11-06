package br.edu.ifam.microsservicos.email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final List<EmailRequest> inbox = new CopyOnWriteArrayList<>();

    public record EmailRequest(@NotBlank @Email String to,
                               @NotBlank String subject,
                               @NotBlank String body) { }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody EmailRequest data) {
        // Apenas grava o pedido na memória. Nenhum e-mail real é enviado.
        inbox.add(data);
        System.out.println("[EMAIL SIMULADO] destinatário=" + data.to()
            + ", assunto=" + data.subject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public List<EmailRequest> history() {
        return List.copyOf(inbox);
    }
}
