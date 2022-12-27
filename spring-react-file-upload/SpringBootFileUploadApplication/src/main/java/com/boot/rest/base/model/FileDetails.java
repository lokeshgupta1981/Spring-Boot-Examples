package com.boot.rest.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file_details")
public class FileDetails {

	// default constructor
	public FileDetails() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String fileName;

	@Column
	private String fileUri;

	@Column
	private String fileDownloadUri;

	@Column
	private long fileSize;

	@Column
	private String uploaderName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileUri() {
		return fileUri;
	}

	public void setFileUri(String fileUri) {
		this.fileUri = fileUri;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUploaderName() {
		return uploaderName;
	}

	public void setUploaderName(String uploaderName) {
		this.uploaderName = uploaderName;
	}

	public FileDetails(String fileName, String fileUri, String fileDownloadUri, long fileSize, String uploaderName) {
		this.fileName = fileName;
		this.fileUri = fileUri;
		this.fileDownloadUri = fileDownloadUri;
		this.fileSize = fileSize;
		this.uploaderName = uploaderName;
	}

	@Override
	public String toString() {
		return "FileDetails [id=" + id + ", fileName=" + fileName + ", fileUri=" + fileUri + ", fileDownloadUri="
				+ fileDownloadUri + ", fileSize=" + fileSize + ", uploaderName=" + uploaderName + "]";
	}

}
