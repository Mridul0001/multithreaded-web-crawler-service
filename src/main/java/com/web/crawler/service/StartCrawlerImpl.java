package com.web.crawler.service;

import com.opencsv.*;
import com.web.crawler.model.PageModel;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StartCrawlerImpl implements StartCrawler,Runnable{
    Thread thread;
    private int MAX_LEVEL;
    String seedUrl;
    HashMap<String, PageModel> foundLinks;
    private String fileName;
    private static final Logger logger = LoggerFactory.getLogger(StartCrawlerImpl.class);
    public StartCrawlerImpl(String seedUrl, int maxLevel){
        logger.info("Started crawling for seed: " + seedUrl);
        this.MAX_LEVEL=maxLevel;
        this.seedUrl=seedUrl;
        this.foundLinks=new HashMap<>();
        thread=new Thread(this);
        thread.start();
    }
    @Override
    public void startCrawling(String seedUrl, int level) {
        if(level<=MAX_LEVEL){
            Document document=getDoc(seedUrl,level);
            if(document!=null){
                for(Element element:document.select("a[href]")){
                    String nextUrl=element.absUrl("href");
                    if(!foundLinks.containsKey(nextUrl)){
                        startCrawling(nextUrl,level+1);
                    }
                }
            }
        }
    }

    private Document getDoc(String seedUrl, int level){
        try {
            Connection connection = Jsoup.connect(seedUrl);
            Document document=connection.get();
            if(connection.response().statusCode()==200){
                String title=document.title();
                PageModel pageModel =new PageModel(title,seedUrl,level);
                logger.info("Title: "+title);
                logger.info("URL: " + seedUrl);
                foundLinks.put(seedUrl, pageModel);
                return document;
            }
            return null;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        if(!alreadyCrawled(this.seedUrl)){
            startCrawling(this.seedUrl,0);
            generateFile();
        }else{
            getDataFromFile();
        }
    }

    private boolean alreadyCrawled(String seedUrl){
        try {
            URL url=new URL(seedUrl);
            this.fileName=url.getHost();
            File file = new File("./"+"src/main/java/com/web/crawler/datafiles/"+this.fileName+".csv");
            return file.exists();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void generateFile(){
        List<String[]> data = new ArrayList<>();

        //Create one row for each found link with seedUrl as parent
        for(Map.Entry<String,PageModel> entry:foundLinks.entrySet()){
            String[] row=new String[4];
            row[0]=this.seedUrl;
            row[1]=entry.getValue().getUrl();
            row[2]=entry.getValue().getTitle();
            row[3]=entry.getValue().getLevel()+"";
            //add created record to data list
            data.add(row);
        }

        //write whole data list to file
        File file = new File("./"+"src/main/java/com/web/crawler/datafiles/"+this.fileName+".csv");
        try {
            file.createNewFile();
            FileWriter fileWriter=new FileWriter(file);
            CSVWriter writer = new CSVWriter(fileWriter,
                    ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getDataFromFile(){
        String path = "./"+"src/main/java/com/web/crawler/datafiles/"+this.fileName+".csv";
        try {
            FileReader fileReader=new FileReader(path);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(parser)
                    .build();
            List<String[]> data = csvReader.readAll();
            for(String[] s:data){
                String url = s[1];
                PageModel model=new PageModel(s[2],s[1],Integer.parseInt(s[3]));
                this.foundLinks.put(url,model);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
