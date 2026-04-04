package psicologa.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DadosAgendamentoDTO {

    private Long pacienteId;
    private LocalDate data;
    private LocalTime hora;
    private String repeticao;
    private List<Integer> diasSemana;

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getRepeticao() { return repeticao; }
    public void setRepeticao(String repeticao) { this.repeticao = repeticao; }

    public List<Integer> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(List<Integer> diasSemana) { this.diasSemana = diasSemana; }
}