package cl.usach.tingeso.valoresservice.REST;

import cl.usach.tingeso.valoresservice.entities.ValoresEntity;
import cl.usach.tingeso.valoresservice.services.ValoresService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class ValoresREST {
    @Autowired
    private ValoresService valoresService;

    @PostMapping(value = "valores/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> save(@ModelAttribute MultipartFile file) {
        return valoresService.saveExcel(file);
    }

    @GetMapping("valores/proveedor/{codigo_proveedor}")
    public ResponseEntity<ValoresEntity> getValoresByCodigoProveedor(@PathVariable String codigo_proveedor) {
        try{
            ValoresEntity valoresEntity = valoresService.getValoresByCodigoProveedor(codigo_proveedor);
            return ResponseEntity.ok(valoresEntity);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
