package br.edu.ifpb.file_service.controller;

import br.edu.ifpb.file_service.dto.FileDTO;
import br.edu.ifpb.file_service.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadFile(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            String fileId = fileService.storeFile(id, file);
            return ResponseEntity.ok("File uploaded successfully with ID:" + fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    /*@GetMapping("/get/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        GridFsResource resource = fileService.getFile(id);

        if (resource != null) {
            try {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(resource.getContentType()))
                        .body(new InputStreamResource(resource.getInputStream()));
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return ResponseEntity.notFound().build();
    }*/

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        try {
            FileDTO fileDto = fileService.getFile(id);
            return new ResponseEntity<>(fileDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile (@PathVariable String id) {
        try {
            GridFsResource resource = fileService.getFileResource(id);
            if (resource != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
                headers.add(HttpHeaders.CONTENT_TYPE, resource.getContentType());

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/")
    public ResponseEntity<?> getAllFiles(){
        try {
            return fileService.getAllFiles();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(path = "/{id}/user_files")
    public ResponseEntity<?> getUserFiles(@PathVariable String id) {
        try {
            return fileService.getUserFiles(id);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile (@PathVariable String id) {
        try {
            fileService.deleteFile(id);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
