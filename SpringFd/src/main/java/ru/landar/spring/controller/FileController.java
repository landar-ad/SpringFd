package ru.landar.spring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.model.IFile;
import ru.landar.spring.service.FileService;
import ru.landar.spring.service.HelperService;

@Controller
public class FileController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	private HelperService hs;
	
	@RequestMapping(value = "/fileView", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getFile(@RequestParam Integer rn) throws Exception {
	    
		IFile f = fileService.getFile(rn);
		if (f == null) throw new Exception("Не найден файл по идентификатору " + rn);
		if (hs.isEmpty(f.getFileuri())) throw new Exception("Не задан путь к файлу для " + rn);
		
		File file = new File(f.getFileuri());
		if (!file.exists()) throw new Exception("Не найден файл на сервере для " + rn);
		
		InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
		
		String fileName = f.getFilename();
		if (hs.isEmptyTrim(fileName)) fileName = file.getName();
		Optional<MediaType> mt = MediaTypeFactory.getMediaType(fileName);
		
		String content = "attachment; filename*=UTF-8''" + URLEncoder.encode(hs.replaceSpecial(fileName), "UTF-8");
		
		return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, content)
	                .contentType(mt.orElse(MediaType.ALL))
	                .contentLength(file.length())
	                .body(isr);
	}
}
