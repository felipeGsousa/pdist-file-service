package br.edu.ifpb.file_service.consumers;

import br.edu.ifpb.file_service.dto.FileDTO;
import br.edu.ifpb.file_service.helpers.CustomMultipartFile;
import br.edu.ifpb.file_service.service.FileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FileConsumer {

    @Autowired
    private FileService fileService;
    @RabbitListener(queues = "${broker.queue.post.file}", containerFactory = "myRabbitListenerContainerFactory", concurrency = "15-40")
    public String listenFileQueue(@Payload FileDTO fileDto) throws IOException {
        MultipartFile file = new CustomMultipartFile(fileDto.getData(), fileDto.getFilename(), fileDto.getContentType());
        String fileId = fileService.storeFile(fileDto.getUserId(), file);
        return fileId;
    }
    @RabbitListener(queues = "${broker.queue.file.post}", containerFactory = "myRabbitListenerContainerFactory", concurrency = "15-40")
    public Map<String, Object> listenPostQueue(@Payload String fileId) {
        FileDTO fileDTO = fileService.getFile(fileId);
        Map<String, Object> fileDtoMap = new HashMap<>();

        fileDtoMap.put("data", fileDTO.getData());
        fileDtoMap.put("contentType", fileDTO.getContentType());
        fileDtoMap.put("filename", fileDTO.getFilename());
        fileDtoMap.put("id", fileDTO.getId());
        fileDtoMap.put("userId", fileDTO.getUserId());

        return fileDtoMap;
    }
}
