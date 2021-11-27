package com.web.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PageModel {
    String title;
    String url;
    int level;
}
