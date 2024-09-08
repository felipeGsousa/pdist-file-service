package br.edu.ifpb.file_service.service;

import br.edu.ifpb.file_service.dto.FileDTO;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile (String userId, MultipartFile file) throws IOException {
        Document metadata = new Document();
        metadata.put("userId", userId);
        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata).toString();
    }

    public GridFsResource getFileResource (String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (gridFSFile != null) {
            return gridFsTemplate.getResource(gridFSFile);
        }
        return null;
    }

    public FileDTO getFile (String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (gridFSFile != null) {
            GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
            try {
                Document metadata = gridFSFile.getMetadata();
                String contentType = "application/octet-stream";

                if (metadata != null && metadata.getString("contentType") != null) {
                    contentType = metadata.getString("contentType");
                } else if (metadata.getString("_contentType") != null) {
                    contentType = metadata.getString("_contentType");
                }

                /*Map<String, Object> response = new HashMap<>();
                response.put("filename", resource.getFilename());
                response.put("contentType", contentType);
                response.put("data",  resource.getInputStream().readAllBytes());*/

                FileDTO fileDTO = new FileDTO();
                fileDTO.setId(id);
                fileDTO.setContentType(contentType);
                fileDTO.setFilename(resource.getFilename());
                fileDTO.setData(resource.getInputStream().readAllBytes());

                return fileDTO;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return null;
    }


    /*public ResponseEntity<?> getFile (String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (gridFSFile != null) {
            GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
            try {
                Document metadata = gridFSFile.getMetadata();
                String contentType = "application/octet-stream";

                if (metadata != null && metadata.getString("contentType") != null) {
                    contentType = metadata.getString("contentType");
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource.getInputStream().readAllBytes());
            } catch (IOException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
    }*/

    /*Map<String, Object> response = new HashMap<>();
        response.put("filename", filename);
        response.put("contentType", contentType);
        response.put("data", Files.readAllBytes(filePath));*/

    public ResponseEntity<?> getAllFiles() {
        Query query = new Query();
        List<GridFSFile> files = gridFsTemplate.find(query).into(new ArrayList<>());
        List<FileDTO> fileDTOS = fsToDto(files);
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
    }

    public ResponseEntity<?> getUserFiles(String userId) {
        Query query = new Query(Criteria.where("metadata.userId").is(userId));
        List<GridFSFile> files = gridFsTemplate.find(query).into(new ArrayList<>());
        List<FileDTO> fileDTOS = fsToDto(files);
        return new ResponseEntity<>(fileDTOS, HttpStatus.OK);
    }

    public void deleteFile (String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public List<FileDTO> fsToDto(List<GridFSFile> files) {
        List<FileDTO> fileDTOS = new ArrayList<>();
        for (GridFSFile file : files) {
            FileDTO fileDto = new FileDTO();
            fileDto.setId(file.getObjectId().toString());
            fileDto.setFilename(file.getFilename());
            fileDto.setContentType(file.getMetadata().getString("contentType"));
            fileDTOS.add(fileDto);
        }
        return fileDTOS;
    }
}
