package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Log;
import com.example.repository.LogRepository;

@Service
public class LogService {

	private final LogRepository logRepository;

	@Autowired
	public LogService(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	public List<Log> findAll() {
		return this.logRepository.findAll();
	}

	public List<Log> findByUserId(Integer userId) {
		return this.logRepository.findByUserId(userId);
	}

	public Log save(Log log) {
	    return this.logRepository.save(log);
	}

	public Log update(Integer libraryId, Integer userId) {
		Log log = this.logRepository.findTopByLibraryIdAndUserIdOrderByRentDateDesc(libraryId, userId);
		log.setReturnDate(LocalDateTime.now());
		return this.logRepository.save(log);
	}
}
