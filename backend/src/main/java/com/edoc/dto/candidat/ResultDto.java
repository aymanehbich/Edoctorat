package com.edoc.dto.candidat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ResultDto<Type> {
    private int count;
    private String next;
    private String previous;
    private List<Type> results;
    private String msg;
    
    public ResultDto(int count,String next,String previous,List<Type> results) {
        this.results = results;
        this.count = results.size();
    }

}
