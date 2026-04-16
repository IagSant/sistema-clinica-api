package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologa.model.Lead;

public interface LeadRepository extends JpaRepository<Lead, Long> {
}