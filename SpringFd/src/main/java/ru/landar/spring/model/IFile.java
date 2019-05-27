package ru.landar.spring.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.web.multipart.MultipartFile;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IFile extends IBase {
	
	public static String singleTitle() { return "Файл"; }
	public static String multipleTitle() { return "Файлы"; }
	
	private String filename;
	private String fileext;
	private SpFileType filetype;
	private String fileuri;
	private Long filelength;
	
	@Column(length=1024)
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; setName(filename); }
    
    @Column(length=10)
    public String getFileext() { return fileext; }
    public void setFileext(String fileext) { this.fileext = fileext; }
    
    @ManyToOne(targetEntity=SpFileType.class, fetch=FetchType.LAZY)
    public SpFileType getFiletype() { return filetype; }
    public void setFiletype(SpFileType filetype) { this.filetype = filetype; }
    
    @Column(length=2048)
    public String getFileuri() { return fileuri; }
    public void setFileuri(String fileuri) { this.fileuri = fileuri; }
    
    public Long getFilelength() { return filelength; }
    public void setFilelength(Long filelength) { this.filelength = filelength; }
    
    @Override
    public Object onUpdate(Map<String, Object> map, Map<String, Object[]> mapChanged) throws Exception { 
    	Object ret = super.onUpdate(map, mapChanged);
    	if (ret != null) return ret;
    	if (map.isEmpty()) return false;
		String filesDirectory = (String)objService.getSettings("filesDirectory", "string");
		if (hs.isEmpty(filesDirectory)) {
			filesDirectory = System.getProperty("user.dir") + File.separator + "FILES";
			System.out.println(filesDirectory);
		}
		for (; ;) {
			MultipartFile fileInput = (MultipartFile)map.get("fileuri");
			if (fileInput == null) break;
			InputStream is = fileInput.getInputStream();
			if (is == null) break;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			long length = hs.copyStream(is, baos, true, true);
			if (length == 0) break;
			is = new ByteArrayInputStream(baos.toByteArray());
			String filename = fileInput.getOriginalFilename();
			if (!hs.isEmpty(filename) && hs.isEmpty(getFilename())) {
				setFilename(filename);
				int k = filename.lastIndexOf('.');
				String fileext = k > 0 ? filename.substring(k + 1) : "";
				if (hs.isEmpty(getFileext())) setFileext(fileext);
				if (getFiletype() == null && getFileext() != null) {
					SpFileType filetype = (SpFileType)objService.getObjByCode(SpFileType.class, getFileext().toLowerCase());
					setFiletype(filetype);
				}
			}
			File fd = new File(filesDirectory + File.separator + getRn());
			fd.mkdirs();
			File ff = new File(fd, getFilename());
			setFilelength(hs.copyStream(is, new FileOutputStream(ff), true, true));
			setFileuri(ff.getAbsolutePath());
			break;
		}
		objService.saveObj(this);
		return true;
    }
}
