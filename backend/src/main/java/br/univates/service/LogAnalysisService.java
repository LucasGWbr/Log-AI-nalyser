package br.univates.service;

import br.univates.dto.AiResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.univates.model.LogAnalysis;
import br.univates.repository.LogAnalysisRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogAnalysisService {

    private final LogAnalysisRepository repository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public LogAnalysisService(LogAnalysisRepository repository, ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.repository = repository;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public LogAnalysis analyzeLogText(String logContent) {

        String cleanedLog = cleanLog(logContent);
        Optional<LogAnalysis> exists = repository.findFirstByLog(cleanedLog);
        if (exists.isPresent()) {
            return exists.get();
        }

        // Prompt enviado pra IA resolver o erro
        String systemPrompt = """
            Você é um Engenheiro de Software Sênior e SRE (Site Reliability Engineer).
            Sua capacidade é analisar logs de qualquer tecnologia (Java, Python, Node, Docker, SQL, System, etc).
            
            OBJETIVO:
            Identifique a tecnologia do log, encontre o erro e forneça a correção mais direta possível.
            
            REGRAS DE FORMATAÇÃO (CRÍTICO):
            1. Responda SEMPRE em Português do Brasil (PT-BR).
            2. PROIBIDO usar Markdown (NÃO use ```json, apenas abra as chaves).
            3. Responda APENAS o JSON cru. Nada de "Aqui está o JSON" ou texto extra.
            4. Se o log não for um erro óbvio, explique o que ele significa.
            5. SEJA CONCISO: Sua resposta deve ser curta e direta para não estourar o limite de texto.
            
            FORMATO DO JSON DE RESPOSTA:
            {
                "resumo": "Explicação curta e grossa do erro (Max 2 linhas).",
                "solucao": "O código corrigido ou comando necessário (Sem explicações extras aqui)."
            }
            """;

        // Chama a IA
        String aiResponse = chatClient.prompt()
                .system(systemPrompt)
                .user("Log para análise: \n" + cleanedLog)
                .options(OllamaOptions.builder()
                        .withNumPredict(4000) // Tokens da IA
                        .withTemperature(0.2)
                        .withFormat("json")
                        .build())
                .call()
                .content();
        // Converte JSON da IA
        LogAnalysis analysis = new LogAnalysis();
        analysis.setLog(cleanedLog);

        try {
            AiResponseDTO dto = objectMapper.readValue(aiResponse, AiResponseDTO.class);

            analysis.setExplanation(dto.resumo());
            analysis.setSuggestion(dto.solucao());
        } catch (Exception e) {
            System.err.println("Resposta da IA: " + aiResponse);
            throw new RuntimeException("Falha ao interpretar resposta da IA: " + e);
        }
        return repository.save(analysis);
    }

    private String cleanLog(String logFull) {
        if (logFull == null || logFull.isBlank()) return "";
        String logCurto = logFull.lines().limit(50).collect(java.util.stream.Collectors.joining("\n"));
        return logCurto
                // Formato ISO: 2024-12-14
                .replaceAll("\\d{4}-\\d{2}-\\d{2}", "[DATA]")
                // Formato BR: 14/12/2024
                .replaceAll("\\d{2}/\\d{2}/\\d{4}", "[DATA]")
                // --- NOVO: Formato Go/Logs: 2024/12/14 ---
                .replaceAll("\\d{4}/\\d{2}/\\d{2}", "[DATA]")
                // Hora: 10:20:30 ou 10:20:30.123
                .replaceAll("\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,3})?", "[HORA]");
    }

}