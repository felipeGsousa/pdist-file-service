package br.edu.ifpb.file_service.service;

import br.edu.ifpb.file_service.dto.FileDto;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile (String userId, MultipartFile file) throws IOException {
        Document metadata = new Document();
        metadata.put("userId", userId);
        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(),metadata).toString();
    }

    public GridFsResource getFile (String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        if (gridFSFile != null) {
            return gridFsTemplate.getResource(gridFSFile);
        }
        return null;
    }

    public ResponseEntity<?> getAllFiles() {
        Query query = new Query();
        List<GridFSFile> files = gridFsTemplate.find(query).into(new ArrayList<>());
        List<FileDto> fileDtos = fsToDto(files);
        return new ResponseEntity<>(fileDtos, HttpStatus.OK);
    }

    public ResponseEntity<?> getUserFiles(String userId) {
        Query query = new Query(Criteria.where("metadata.userId").is(userId));
        List<GridFSFile> files = gridFsTemplate.find(query).into(new ArrayList<>());
        List<FileDto> fileDtos = fsToDto(files);
        return new ResponseEntity<>(fileDtos, HttpStatus.OK);
    }

    public void deleteFile (String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public List<FileDto> fsToDto(List<GridFSFile> files) {
        List<FileDto> fileDtos = new ArrayList<>();
        for (GridFSFile file : files) {
            FileDto fileDto = new FileDto();
            fileDto.setId(file.getObjectId().toString());
            fileDto.setFilename(file.getFilename());
            fileDto.setContentType(file.getMetadata().getString("contentType"));
            fileDto.setLength(file.getLength());
            fileDtos.add(fileDto);
        }
        return fileDtos;
    }
}
