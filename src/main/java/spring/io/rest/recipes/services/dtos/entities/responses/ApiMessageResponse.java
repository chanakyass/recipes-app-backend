package spring.io.rest.recipes.services.dtos.entities.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiMessageResponse {
    Long generatedId;
    String message;
    LocalDateTime timestamp;

    public ApiMessageResponse(String message){
        this.generatedId = null;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiMessageResponse(Long generatedId){
        this.generatedId = generatedId;
        this.message = "Action successful";
        this.timestamp = LocalDateTime.now();
    }

    public ApiMessageResponse(){
        this.generatedId = null;
        this.message = "Action successful";
        this.timestamp = LocalDateTime.now();
    }

    public static ApiMessageResponse defaultSuccessResponse(){
        return new ApiMessageResponse();
    }

    public static ApiMessageResponse defaultCreationSuccessResponse(Long generatedId){
        return new ApiMessageResponse(generatedId);
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
