package br.edu.ifam.microsservicos.eventos.exception;

import br.edu.ifam.microsservicos.eventos.dto.ErrorResponseDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notFound(EventNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDTO("EVENTO_NAO_ENCONTRADO", e.getMessage()));
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ErrorResponseDTO> full(EventFullException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponseDTO("EVENTO_LOTADO", e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDTO> invalid(Exception e) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponseDTO("DADOS_INVALIDOS", "Confira os dados enviados e a data no formato dd/MM/yyyy."));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDTO> emailUnavailable(FeignException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponseDTO("EMAIL_INDISPONIVEL", "O serviço de e-mail não respondeu. Verifique a porta 8090."));
    }
}
