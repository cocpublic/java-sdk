package io.modelcontextprotocol.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class McpRequestContext {

	private static final Logger logger = LoggerFactory.getLogger(McpRequestContext.class);

	// 存储当前线程的sessionId
	private static final ThreadLocal<String> sessionIdHolder = new ThreadLocal<>();

	// 全局参数映射: sessionId -> Map<参数名, 参数值>
	private static final Map<String, Map<String, String>> globalParamsMap = new ConcurrentHashMap<>();

	/**
	 * 设置会话ID
	 */
	public static void setSessionId(String sessionId) {
		sessionIdHolder.set(sessionId);
	}

	/**
	 * 获取会话ID
	 */
	public static String getSessionId() {
		logger.debug("Aux getSessionId = " + sessionIdHolder.get() + "threadId = " + Thread.currentThread().getId());
		return sessionIdHolder.get();
	}

	/**
	 * 清除会话ID
	 */
	public static void clearSessionId() {
		logger.debug("Aux clearSessionId = " + sessionIdHolder.get() + "threadId = " + Thread.currentThread().getId());
		sessionIdHolder.remove();
	}

	/**
	 * 添加单个参数
	 */
	public static void addParam(String key, String value) {
		String sessionId = getSessionId();
		if (sessionId == null) {
			logger.warn("尝试添加参数时sessionId为空");
			return;
		}

		globalParamsMap.computeIfAbsent(sessionId, k -> new HashMap<>()).put(key, value);
	}

	/**
	 * 添加多个参数
	 */
	public static void addParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return;
		}

		String sessionId = getSessionId();
		if (sessionId == null) {
			logger.warn("尝试添加多个参数时sessionId为空");
			return;
		}

		globalParamsMap.computeIfAbsent(sessionId, k -> new HashMap<>()).putAll(params);
	}

	/**
	 * 获取单个参数
	 */
	public static String getParam(String key) {
		String sessionId = getSessionId();
		if (sessionId == null) {
			return null;
		}

		Map<String, String> params = globalParamsMap.get(sessionId);
		return params != null ? params.get(key) : null;
	}

	/**
	 * 获取当前会话的所有参数
	 */
	public static Map<String, String> getParams() {
		String sessionId = getSessionId();
		if (sessionId == null) {
			return Collections.emptyMap();
		}

		Map<String, String> params = globalParamsMap.get(sessionId);
		return params != null ? Collections.unmodifiableMap(params) : Collections.emptyMap();
	}

	public static int getAllSize() {
		return globalParamsMap.size();
	}

	/**
	 * 清除当前会话的参数
	 */
	public static void clearParams(String sessionId) {
		logger.debug("Aux clearParams sessionId = " + sessionId);
		if (sessionId != null) {
			globalParamsMap.remove(sessionId);
		}
	}

}