package com.web.crawler.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class ResponseModel {
    HashMap<String, List<PageModel>> response;
}
