package com.edoc.dto.inscription;

import java.util.List;

import lombok.Data;

@Data 
public class PagedResponse<T> {
    private List<T> results;
    private Integer count;
    private String next;
    private String previous;
}
