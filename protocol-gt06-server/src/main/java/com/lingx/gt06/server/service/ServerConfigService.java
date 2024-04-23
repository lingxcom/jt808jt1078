package com.lingx.gt06.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lingx.jt808.core.service.DatabaseConfigService;

@Component
public class ServerConfigService {

	@Value("#{configs['gt06.server.port']}")
	private String serverPort="6602";
	@Autowired
	private DatabaseConfigService databaseConfigService;
	public String getServerPort() {
		return serverPort;
	}

	public String getSavePhotoPath() {
		return this.databaseConfigService.getConfigValue("lingx.jt808.photo.path", "C:/");
	}


	public String getSaveOriginalHexstring() {
		return this.databaseConfigService.getConfigValue("lingx.jt808.save.original", "false");
	}
}
