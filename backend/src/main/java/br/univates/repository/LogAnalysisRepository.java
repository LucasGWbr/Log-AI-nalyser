package br.univates.repository;

import br.univates.model.LogAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogAnalysisRepository extends JpaRepository<LogAnalysis, Long> {

    Optional<LogAnalysis> findFirstByLog(String log);
}