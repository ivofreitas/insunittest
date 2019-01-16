package br.com.api.gurgel.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token")
public class Token {

    @Id
    private Long id;
    
    private String deviceId;

    private String tokenKey;
}
