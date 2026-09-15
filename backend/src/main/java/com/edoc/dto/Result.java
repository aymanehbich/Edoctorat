package com.edoc.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Long count;
    private String next;
    private String previous;
    private List<T> results;
    
    public void setCount(long totalElements) {
      this.count = totalElements;
    }

    public Result(int count, String next, String previous, List<T> results) {
        this.count = (long) count; 
        this.next = next;
        this.previous = previous;
        this.results = results != null ? results : new ArrayList<>();
    }

    public Result(long count, String next, String previous, List<T> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results != null ? results : new ArrayList<>();
    }
}