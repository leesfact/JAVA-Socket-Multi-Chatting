package Dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class RequestDto<T> {
	private String resource;
	private T body;
}


