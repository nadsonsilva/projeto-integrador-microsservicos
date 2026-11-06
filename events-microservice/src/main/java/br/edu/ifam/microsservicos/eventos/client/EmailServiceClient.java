package br.edu.ifam.microsservicos.eventos.client;

import br.edu.ifam.microsservicos.eventos.dto.EmailRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service", url = "${email.service.url}")
public interface EmailServiceClient {
    @PostMapping("/api/email/send")
    void sendEmail(@RequestBody EmailRequestDTO request);
}
