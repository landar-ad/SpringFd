package ru.landar.spring.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.UserService;

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
    	for (; ;) {
    		String filename = getFilename();
    		if (hs.isEmpty(filename)) {
    			String fileuri = getFileuri();
    			if (hs.isEmpty(fileuri)) break;
				int k = fileuri.lastIndexOf('/');
				if (k < 0) k = fileuri.lastIndexOf('\\');
				if (k > 0) {
					int s = fileuri.indexOf('_', k);
					if (s > 0) k = s;
					filename = fileuri.substring(k + 1);
					setFilename(filename);
				}
			}
    		if (hs.isEmpty(filename)) break;
	    	String fileext = getFileext();
			if (hs.isEmpty(fileext)) {
				int k = filename.lastIndexOf('.');
				fileext = k > 0 ? filename.substring(k + 1) : "";
				setFileext(fileext);
			}
			if (!hs.isEmpty(fileext) && getFiletype() == null) {
				SpFileType filetype = (SpFileType)objService.getObjByCode(SpFileType.class, fileext.toLowerCase());
				setFiletype(filetype);
			}
			break;
    	}
		return true;
    }
    @Autowired
	UserService userService;
    @Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
     	Integer rn = getRn();
    	if (rn == null) return true;
    	if (getParent() != null) return getParent().onCheckRights(op);
    	if (getCreator() != null) return getCreator().equals(userService.getPrincipal());
    	return true;
    }
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		
		try {
			model.addAttribute("listFileType", objService.findAll(SpFileType.class));
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onRedirectAfterUpdate() { 
    	Object ret = invoke("onRedirectAfterUpdate");
    	if (ret != null) return ret;
    	return getParent() != null ? "/detailsObj?clazz=Document" + "&rn=" + getParent().getRn() : super.onRedirectAfterUpdate();
    }
}
