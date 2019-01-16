package br.com.api.gurgel.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String platform;
    private String topicName;
    private String title;
	private String message;
}
