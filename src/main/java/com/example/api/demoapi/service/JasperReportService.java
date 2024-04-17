package com.example.api.demoapi.service;

import com.example.api.demoapi.model.Product;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {
    public byte[] getProductReport(List<Product> products, String Format) throws JRException {
        JasperReport jasperReport;

        try {
            jasperReport = (JasperReport) JRLoader.loadObject(ResourceUtils.getFile("item-report.jasper"));
        }catch (FileNotFoundException | JRException e) {
            try {
                File file = ResourceUtils.getFile("classpath:item-report.jrxml");
                jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
                JRSaver.saveObject(jasperReport, "item-report.jasper");
            } catch (FileNotFoundException | JRException err) {
                throw new RuntimeException(err);
            }
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Item Report");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
