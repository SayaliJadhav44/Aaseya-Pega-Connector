package io.camunda.example.dto;

import io.camunda.connector.generator.java.annotation.TemplateProperty;
import io.camunda.connector.generator.java.annotation.TemplateProperty.PropertyType;
import jakarta.validation.constraints.NotEmpty;

public record AISPegaRequest(@NotEmpty @TemplateProperty(group = "input", type = PropertyType.Text) String url,
		@TemplateProperty(group = "input", type = PropertyType.Text) Object payload,
		@NotEmpty @TemplateProperty(group = "input", type = PropertyType.Text) String method,
		@NotEmpty @TemplateProperty(group = "authentication", type = PropertyType.Text) String username,
		@NotEmpty @TemplateProperty(group = "authentication", type = PropertyType.Text) String password
//		@NotNull Authentication authentication
) {
}
