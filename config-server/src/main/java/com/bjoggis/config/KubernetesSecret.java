package com.bjoggis.config;

import java.util.Map;

public record KubernetesSecret(String namespace, String name, Map<String, String> data) {}
