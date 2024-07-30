package com.seth.draft_assistant.model.internal.requests;
import com.seth.draft_assistant.model.internal.interfaces.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayerUpdateRequest implements Identifiable {
    private Long id;
    private Integer age;
    private Integer positionalDepth;
    private String notes;
    private Boolean isSleeper;
    private Double ecr;

    @Override
    public Long getId() {
        return this.id;
    }
}
