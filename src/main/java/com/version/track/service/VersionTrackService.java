package com.version.track.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poiji.bind.Poiji;
import com.version.track.model.Doc;
import com.version.track.model.Product;
import com.version.track.model.Root;
import com.version.track.model.XLMavenIds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VersionTrackService {
    @Autowired
    private RestTemplate restTemplate;
    public String getVersionNumbers() {
        log.info("process started");
        List<String> listId = new ArrayList<>();
        List<String> listNames = new ArrayList<>();
        List<XLMavenIds> xlMavenIds = Poiji.fromExcel(new File("C:\\Users\\Ciber\\DATA\\FEED\\VERSION_SHEET\\versiontrackinput.xlsx"), XLMavenIds.class);
        for (int i = 0; i < xlMavenIds.size(); i++) {
            listId.add(xlMavenIds.get(i).getId());
            if(xlMavenIds.get(i).getName() != null && !xlMavenIds.get(i).getName().isEmpty())
                listNames.add(xlMavenIds.get(i).getName());
        }
        HashMap<String, Doc> versionsMap = new LinkedHashMap<>();
        HashMap<String,Product> productMap = new LinkedHashMap<>();
        HashMap<String,Product> productLtsMap = new LinkedHashMap<>();
        for (int i = 0; i < listId.size(); i++) {
            String response = null;
            String id = listId.get(i);
            String mavenId = "\""+id+"\"";
            try {
                response = restTemplate.getForObject("https://search.maven.org/solrsearch/select?q=id:"+mavenId+"&rows=20&wt=json", String.class);
            } catch (RestClientException e) {
                response = null;
                //versionsMap.put(id,"please check manually");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if(response != null) {
                try {
                    Root readValue = mapper.readValue(response, Root.class);
                    if (readValue != null) {
                        log.info("maven id is : {}", id);
                        Doc doc = readValue.response.docs.get(0);
                        versionsMap.put(id, doc);
                    }
                } catch (Exception e) {
                    log.info("exception occurred while getting versoin numbers : {}", e.getMessage());
                    Doc doc1 = new Doc();
                    doc1.setLatestVersion("please check manually");
                    doc1.setTimestamp(new Date().getTime());
                    versionsMap.put(id, doc1);
                }
            }
        }
        updateversionsForOther(listNames,productMap,productLtsMap);
        writeDataIntoSheet(versionsMap,productMap,productLtsMap);
       return "COMPLETED";
    }

    private void updateversionsForOther(List<String> listNames, HashMap<String, Product> productMap, HashMap<String, Product> productLtsMap) {
        for (int i = 0; i < listNames.size(); i++) {
            String response = null;
            String product = listNames.get(i);
            response = restTemplate.getForObject("https://endoflife.date/api/"+product+".json", String.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Product productObject = null;
            Product productTempObject = null;
            try {
                Product[] readValue = mapper.readValue(response, Product[].class);
                if(readValue != null){
                    log.info("product is : {}",product);
                    productObject =  readValue[0];
                    productMap.put(product,productObject);
                    float intialCycle = 0;
                    for (int j = 0; j < readValue.length ; j++){
                        if(readValue[j].getLts().equalsIgnoreCase("true")){
                            if(Float.valueOf(readValue[j].cycle) > intialCycle) {
                                intialCycle = Float.valueOf(readValue[j].cycle);
                                productTempObject = readValue[j];
                                productLtsMap.put(product, productTempObject);
                            }
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                log.info("exception occurred for product : {}",product);
                log.info("exception message is : {}",e.getMessage());
            }
        }
    }

    private void writeDataIntoSheet(HashMap<String, Doc> versionsMap, HashMap<String, Product> productMap, HashMap<String, Product> productLtsMap) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet
                = workbook.createSheet("version track sheet");
        int rownum = 0;
        Row rowt = sheet.createRow(rownum++);
        int cellnumt = 0;
        Cell cellt = rowt.createCell(cellnumt++);
        cellt.setCellValue("Id");
        Cell cell1t = rowt.createCell(cellnumt++);
        cell1t.setCellValue("Latest Version");
        Cell cell2t = rowt.createCell(cellnumt++);
        cell2t.setCellValue("Release Date");
        Cell cell3t = rowt.createCell(cellnumt++);
        cell3t.setCellValue("Support");
        Cell cell4t = rowt.createCell(cellnumt++);
        cell4t.setCellValue("LTS");
        Cell cell5t = rowt.createCell(cellnumt);
        cell5t.setCellValue("EOL");
        DateFormat f = new SimpleDateFormat("dd-MMM-yyy");
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (Map.Entry<String,Doc> entry : versionsMap.entrySet()) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(entry.getKey());
            Cell cell1 = row.createCell(cellnum++);
            cell1.setCellValue(entry.getValue().latestVersion);
            if(entry.getValue() != null) {
                Cell cell2 = row.createCell(cellnum);
                cell2.setCellValue(f.format(new Date(new Timestamp(entry.getValue().timestamp).getTime())));
            }
        }
        for (Map.Entry<String,Product> entry : productMap.entrySet()) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(entry.getKey());
            Cell cell1 = row.createCell(cellnum++);
            cell1.setCellValue(entry.getValue().getLatest());
            try {
            Cell cell2 = row.createCell(cellnum++);
            cell2.setCellValue(f.format(f1.parse(entry.getValue().getReleaseDate())));
            Cell cell3 = row.createCell(cellnum++);
            cell3.setCellValue(entry.getValue().getSupport());
            Cell cell4 = row.createCell(cellnum++);
            cell4.setCellValue(entry.getValue().getLts());
            Cell cell5 = row.createCell(cellnum);
            cell5.setCellValue(entry.getValue().getEol());
            } catch (ParseException e) {
            }
            if(productLtsMap.containsKey(entry.getKey()) && entry.getValue().getLts().equalsIgnoreCase("false")){
                Row newRow = sheet.createRow(rownum++);
                int cellnumi = 1;
                Cell celli2 = newRow.createCell(cellnumi++);
                celli2.setCellValue(productLtsMap.get(entry.getKey()).getLatest());
                Cell celli3 = newRow.createCell(cellnumi++);
                try {
                    celli3.setCellValue(f.format(f1.parse(productLtsMap.get(entry.getKey()).getReleaseDate())));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Cell celli4 = newRow.createCell(cellnumi++);
                celli4.setCellValue(productLtsMap.get(entry.getKey()).getSupport());
                Cell celli5 = newRow.createCell(cellnumi++);
                celli5.setCellValue(productLtsMap.get(entry.getKey()).getLts());
                Cell celli6 = newRow.createCell(cellnumi);
                celli6.setCellValue(productLtsMap.get(entry.getKey()).getEol());
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(
                    new File("C:\\Users\\Ciber\\DATA\\FEED\\VERSION_SHEET\\versiontrackoutput.xlsx"));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
