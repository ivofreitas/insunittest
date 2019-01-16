package br.com.api.gurgel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Builder
@Document(collection = "notification")
public class NotificationResponse {
	
	@Id
	@JsonIgnore
    private Long id;
	private String title;
	private String message;

	public NotificationResponse(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public NotificationResponse(Long id, String title, String message) {
		this.id = id;
		this.title = title;
		this.message = message;
	}
}
