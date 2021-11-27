package com.web.crawler.controller;

import com.web.crawler.model.PageModel;
import com.web.crawler.model.RequestModel;
import com.web.crawler.model.ResponseModel;
import com.web.crawler.service.StartCrawlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class CrawlerController {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);
    @PostMapping("/start")
    public ResponseEntity startCrawling(@Valid @RequestBody RequestModel requestModel){
        try {
            ArrayList<StartCrawlerImpl> crawlers = new ArrayList<>();
            startMultipleCrawlers(crawlers, requestModel);
            ResponseModel response=generateResponse(crawlers);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    private void startMultipleCrawlers(ArrayList<StartCrawlerImpl> crawlers, RequestModel requestModel){
        //Start one thread for each seed
        for(String s:requestModel.getSeeds()){
            crawlers.add(new StartCrawlerImpl(s,requestModel.getMaxLevel()));
        }

        for(StartCrawlerImpl crawler:crawlers){
            try {
                crawler.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseModel generateResponse(ArrayList<StartCrawlerImpl> crawlers){
        //Resolve the crawled websites data for api response
        ResponseModel response = new ResponseModel();
        HashMap<String, List<PageModel>> responseModel = new HashMap<>();
        for(StartCrawlerImpl crawler: crawlers){
            HashMap<String, PageModel> urls=crawler.getFoundLinks();
            List<PageModel> list=new ArrayList<>();
            for(Map.Entry<String, PageModel> entry:urls.entrySet()){
                list.add(entry.getValue());
            }
            responseModel.put(crawler.getSeedUrl(),list);
        }
        response.setResponse(responseModel);

//        for(Map.Entry<String, List<PageModel>> entry:response.getResponse().entrySet()){
//            logger.info("Seed: "+entry.getKey());
//            for(PageModel model: entry.getValue()){
//                logger.info(model.getTitle()+", "+model.getUrl());
//            }
//        }
        return response;
    }
}
