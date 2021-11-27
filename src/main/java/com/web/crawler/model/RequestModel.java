package com.web.crawler.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class RequestModel {

    @NotNull
    private List<String> seeds;

    @NotNull
    @Max(2)
    private Integer maxLevel;
}
