package ru.landar.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.landar.spring.model.IFile;

@Service
public class FileServiceImpl implements FileService {

	@Autowired 
	ObjService objService;
	
	@Override
	public IFile getFile(Integer rn) {
		
		return (IFile)objService.find(IFile.class, rn);
	}
}
