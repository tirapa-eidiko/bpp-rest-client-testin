package com.eidiko.tests.core;

public class SeedFilesMetaData {

	private String seedFiles[];

	private String path;

	private String schema;

	public SeedFilesMetaData(String path, String seedFiles[], String schema) {
		super();
		this.seedFiles = seedFiles;
		this.schema = schema;
		this.path = path;
	}

	public String getSchema() {
		return schema;
	}

	public String[] getSeedFiles() {
		return seedFiles;
	}

	public String getPath() {
		return path;
	}

}
