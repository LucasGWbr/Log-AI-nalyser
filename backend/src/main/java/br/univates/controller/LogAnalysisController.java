package br.univates.controller;

import br.univates.dto.LogRequestDTO;
import br.univates.model.LogAnalysis;
import br.univates.service.LogAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/log")
public class LogAnalysisController {

    private final LogAnalysisService service;

    public LogAnalysisController(LogAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody LogRequestDTO request) {
        if (request.logContent() == null || request.logContent().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            LogAnalysis result = service.analyzeLogText(request.logContent());
            return ResponseEntity.ok(result);
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("O serviço de IA está fora do ar");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a resposta da IA: " + e.getMessage());
        }
    }
}