package com.boot.rest.base.payload;

public class FileUploadResponse {
	private int id;
	private String fileName;
	private String fileUri;
	private String fileDownloadUri;
	private long fileSize;

	// default constructor
	public FileUploadResponse() {
	}

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

	public FileUploadResponse(int id, String fileName, String fileUri, String fileDownloadUri, long fileSize) {
		this.id = id;
		this.fileName = fileName;
		this.fileUri = fileUri;
		this.fileDownloadUri = fileDownloadUri;
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "FileUploadResponse [fileName=" + fileName + ", fileUri=" + fileUri + ", fileDownloadUri="
				+ fileDownloadUri + ", fileSize=" + fileSize + "]";
	}
}
