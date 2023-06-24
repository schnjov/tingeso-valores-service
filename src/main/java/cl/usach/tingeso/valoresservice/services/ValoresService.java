package cl.usach.tingeso.valoresservice.services;

import cl.usach.tingeso.valoresservice.ValoresServiceApplication;
import cl.usach.tingeso.valoresservice.entities.ValoresEntity;
import cl.usach.tingeso.valoresservice.repositories.ValoresRepository;
import lombok.Generated;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ValoresService {
    @Autowired
    private ValoresRepository valoresRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final Logger logger = Logger.getLogger(ValoresServiceApplication.class.getName());

    @Value("${proveedor.service.base.url}")
    private String proveedorServiceBaseUrl;

    @Generated
    public ResponseEntity<Void> saveExcel(MultipartFile file) {
        logger.info("Guardando valores para proveedores");
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<ValoresEntity> valoresAcopioEntityList = excelToList(sheet);
            valoresRepository.saveAll(valoresAcopioEntityList);
            return ResponseEntity.created(URI.create("/valores")).build();
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, e.toString());
            return ResponseEntity.status(400).build();
        }
    }

    @Generated
    public List<ValoresEntity> excelToList(Sheet sheet) {
        List<ValoresEntity> valoresAcopioEntityList = new ArrayList<>();
        int counter = 0;
        while (counter <= sheet.getLastRowNum()) {
            Row row = sheet.getRow(counter);
            if (row != null) { // verifica si la fila no está vacía
                if (counter > 0) { // omite la primera fila (encabezados de columna)
                    Cell proveedorCell = row.getCell(0);
                    Cell grasaCell = row.getCell(1);
                    Cell solidosCell = row.getCell(2);
                    if (proveedorCell == null || grasaCell == null || solidosCell == null) {
                        break;
                    }
                    int grasa = (int) grasaCell.getNumericCellValue();
                    int solidos = (int) solidosCell.getNumericCellValue();
                    String proveedorCodigo = proveedorCell.getStringCellValue();
                    //Aqui se valida que existe un proveedor con el codigo ingresado
                    String url = proveedorServiceBaseUrl + "exist/" + proveedorCodigo;
                    ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
                    boolean exists = Boolean.TRUE.equals(response.getBody());
                    if (exists) {
                        String id = UUID.randomUUID().toString();
                        if (valoresRepository.findById(id).isPresent()) {
                            id = UUID.randomUUID().toString();
                        }
                        ValoresEntity valoresAcopio = new ValoresEntity(id, proveedorCodigo, grasa, solidos);
                        valoresAcopioEntityList.add(valoresAcopio);
                    }
                }
            }
            counter++;
        }
        return valoresAcopioEntityList;
    }
}
