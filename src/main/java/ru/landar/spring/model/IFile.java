package ru.landar.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

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
}
